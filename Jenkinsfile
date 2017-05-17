properties(
    [
        [$class: 'BuildConfigProjectProperty', name: '', namespace: '', resourceVersion: '', uid: ''],
        buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '15', daysToKeepStr: '', numToKeepStr: '30')),
        disableConcurrentBuilds(),
        [$class: 'HudsonNotificationProperty', enabled: false],
        [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false],
        parameters(
            [
             string(defaultValue: 'f25', description: 'Branch to run against', name: 'BRANCH'),
             string(description: 'fedmsg msg', name: 'CI_MESSAGE')
            ]
        ),
        [$class: 'ThrottleJobProperty', categories: [], limitOneJobWithMatchingParams: false, maxConcurrentPerNode: 0, maxConcurrentTotal: 0, paramsToUseForLimit: '', throttleEnabled: false, throttleOption: 'project'],
        pipelineTriggers(
            [[$class: 'CIBuildTrigger', checks: [[expectedValue: '".+compose_id.+Fedora-Atomic.+"', field: 'compose']], providerName: 'fedmsg', selector: 'org.fedoraproject.prod.fedimg.image.upload']]
        )
    ]
)



node('aos-ci-cd-slave') {
    ansiColor('xterm') {
        timestamps {
              deleteDir()
              currentBuild.description = "${BRANCH}"
              stage('dist-git-trigger') {
                echo "dist-git-trigger"
                dir('ci-pipeline') {
                  git 'https://github.com/CentOS-PaaS-SIG/ci-pipeline'
                }
                dir('cciskel') {
                  git 'https://github.com/cgwalters/centos-ci-skeleton'
                }
                dir('sig-automic-buildscripts') {
                  git 'https://github.com/CentOS/sig-atomic-buildscripts'
                }
                sh '''
                    #!/bin/bash

                    if [[ "${BRANCH}" =~ "^(f25|f26)$" ]]; then
                        echo "Not the Branch we want: ${BRANCH}"
                        exit 0
                    fi

                    virtualenv $WORKSPACE/ci-venv
                    . $WORKSPACE/ci-venv/bin/activate
                    #pip install ansible
                    echo "BRANCH = ${BRANCH}"
                    ls

                    touch $WORKSPACE/CI_MESSAGE.txt
                    touch $WORKSPACE/trigger.downstream
                '''
                archiveArtifacts allowEmptyArchive: true, artifacts: '*.txt,*.downstream'
              }
              if (fileExists("${env.WORKSPACE}/trigger.downstream")) {
                    stage('rpm-build') {
                        echo "rpm-build"
                        sh '''
                            ls -l cciskel
                        '''
                        buildDuffy()
                        archiveArtifacts allowEmptyArchive: true, artifacts: '*.txt,*.props'
                    }
                    stage('ostree-compose-tree') {
                        echo "ostree-compose-tree"
                        sh '''
                            ls -l ci-pipeline
                        '''
                    }
                    stage('ostree-compose-image') {
                        echo "ostree-compose-image"
                        sh '''
                            ls -l sig-atomic-buildscripts
                        '''
                    }
              }
        }
    }
}


def buildDuffy() {
  sh '''
    cat > duffy-allocate.props << EOF
    ORIGIN_WORKSPACE=${WORKSPACE}
    ORIGIN_BUILD_TAG=${BUILD_TAG}
    ORIGIN_CLASS=builder
    DUFFY_JOB_TIMEOUT_SECS=3600
  '''
}

