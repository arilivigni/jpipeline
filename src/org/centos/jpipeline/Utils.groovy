#!/usr/bin/groovy
package org.centos.jpipeline

/**
 * Wrapper function to allocate duffy resources using cciskel
 */
def allocateDuffyCciskel(stage) {
    duffyCciskel(stage, '--allocate')
}

/**
 * Wrapper function to teardown duffy resources using cciskel
 */
def teardownDuffyCciskel(stage) {
    duffyCciskel(stage, '--teardown')
}

/**
 * Method for allocating and tearing down duffy resources using https://github.com/cgwalters/centos-ci-skeleton
 * Pass a map to the library
 * duffyMap defaults:
 *  duffyMap[stage:'duffyCciskel-stage', originClass:'builder', duffyTimeoutSecs:'3600,
 *           duffyOps:'', subDir:'cciskel',
 *           repoUrl:'https://github.com/cgwalters/centos-ci-skeleton'
 */
def duffyCciskel(duffyMap) {

    env.ORIGIN_WORKSPACE = "${env.WORKSPACE}/${duffyMap.get('stage','duffyCciskel-stage')}"
    env.ORIGIN_BUILD_TAG = "${env.BUILD_TAG}-${duffyMap.get('stage','duffyCciskel-stage')}"
    env.ORIGIN_CLASS = "${duffyMap.get('originClass','builder')}"
    env.DUFFY_JOB_TIMEOUT_SECS = "${duffyMap.get('duffyTimeoutSecs','3600')}"
    env.DUFFY_OP = "${duffyMap.get('duffyOps','')}"
    echo "Currently in stage: ${stage} ${env.DUFFY_OP} resources"

    if (! (fileExists(duffyMap.get('subDir','cciskel'))) ){
        dir(subDir) {
            git duffyMap.get('repoUrl','https://github.com/cgwalters/centos-ci-skeleton')
        }
    }

//    withCredentials([file(credentialsId: duffyMap.get('duffyKey','duffy-key'), variable: 'DUFFY_KEY')]) {
    withCredentials([file(credentialsId: 'duffy-key', variable: 'DUFFY_KEY')]) {
        sh '''
                #!/bin/bash
                set -xeuo pipefail
        
                cp ${DUFFY_KEY} ~/duffy.key
                chmod 600 ~/duffy.key
    
                mkdir -p ${ORIGIN_WORKSPACE}
                # If we somehow got called without an op, do nothing.
                if test -z "${DUFFY_OP:-}"; then
                  exit 0
                fi
                if test -n "${ORIGIN_WORKSPACE:-}"; then
                  pushd ${ORIGIN_WORKSPACE}
                fi
                if test -n "${ORIGIN_CLASS:-}"; then
                    exec ${WORKSPACE}/cciskel/cciskel-duffy ${DUFFY_OP} --prefix=ci-pipeline 
                        --class=${ORIGIN_CLASS} --jobid=${ORIGIN_BUILD_TAG} \
                        --timeout=${DUFFY_JOB_TIMEOUT_SECS:-0} --count=${DUFFY_COUNT:-1}
                else
                    exec ${WORKSPACE}/cciskel/cciskel-duffy ${DUFFY_OP}
                fi
                exit
        '''
    }
}

/**
 * Convert bash shell properties to groovy
 * shellFile - Pass a shell formatted properties file
 */
def convertProps(shellFile) {
    def command = $/awk -F'=' '{print "env."$1"=\""$2"\""}' ${shellFile} > ${shellFile}.groovy/$
    sh command

    return "${shellFile}.groovy"
}

// ensure we return 'this' on last line to allow this script to be loaded into flows
return this