properties(
        [
                buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '15', daysToKeepStr: '', numToKeepStr: '30')),
                disableConcurrentBuilds(),
        ]
)


node('aos-ci-cd-slave') {
    ansiColor('xterm') {
        timestamps {
            try {
                deleteDir()
                stage('stage-one') {
                    def stage = 'stage-one'
                    writeFile file: "${env.WORKSPACE}/job-${stage}.props",
                            text: "RSYNC_PASSWORD=21331f76\n" +
                                    "DUFFY_HOST=n33.crusty.ci.centos.org\n"
                    def props_file = "${env.WORKSPACE}/job-${stage}.props"
                    def new_props_file = "${env.WORKSPACE}/job-${stage}-new.props"
                    convertProps(props_file, new_props_file)
                    env.ANSIBLE_HOST_KEY_CHECKING = "False"
                    load("${WORKSPACE}/job-${stage}-new.props")
                    sh '''
                            #echo "test"
                            echo "HOST=${DUFFY_HOST}"
                            echo "ATEST=${ANSIBLE_HOST_KEY_CHECKING}"
                        '''
                    currentBuild.result = 'SUCCESS'
                }
                stage('stage-two') {
                    def stage = 'stage-two'
                    writeFile file: "${env.WORKSPACE}/job-${stage}.props",
                            text: "RSYNC_PASSWORD=245554f76\n" +
                                    "DUFFY_HOST=n46.crusty.ci.centos.org\n"
                    def props_file = "${env.WORKSPACE}/job-${stage}.props"
                    def new_props_file = "${env.WORKSPACE}/job-${stage}-new.props"
                    convertProps(props_file, new_props_file)
                    env.ANSIBLE_HOST_KEY_CHECKING = "True"
                    load("${WORKSPACE}/job-${stage}-new.props")
                    sh '''
                            #echo "test"
                            echo "HOST=${DUFFY_HOST}"
                            echo "ATEST=${ANSIBLE_HOST_KEY_CHECKING}"
                        '''
                    currentBuild.result = 'SUCCESS'
                }
            } catch (e) {
                // if any exception occurs, mark the build as failed
                currentBuild.result = 'FAILURE'
                throw e
            } finally {
                currentBuild.displayName = "Build# - ${env.BUILD_NUMBER}"
                currentBuild.description = "${currentBuild.result}"
                emailext subject: "${env.JOB_NAME} - Build # ${env.BUILD_NUMBER} - STATUS = ${currentBuild.result}", to: "ari@redhat.com", body: "This pipeline was a ${currentBuild.result}"
                step([$class: 'ArtifactArchiver', artifacts: '*.props', allowEmptyArchive: true])
            }
        }
    }
}

def convertProps(file1, file2) {
    def command = $/awk -F'=' '{print "env."$1"=\""$2"\""}' ${file1} > ${file2}/$
    sh command
}

