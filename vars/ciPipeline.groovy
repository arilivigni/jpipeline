def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    node {
        // Clean workspace before doing anything
        deleteDir()

        try {
            stage ('HelloStage') {
                echo "Hello ${config.mainTopic}"
            }
            stage ('Build') {
                sh "echo 'building ${config.targetBranch} ...'"
            }
            stage ('Tests') {
                parallel 'static': {
                    sh "echo 'shell scripts to run static tests...'"
                },
                        'unit': {
                            sh "echo 'shell scripts to run unit tests...'"
                        },
                        'integration': {
                            sh "echo 'shell scripts to run integration tests...'"
                            sh '''
                                env
                            '''
                            env.topic = "${config.mainTopic}.ci.package.complete"
                            echo "${env.topic}"
                            sh '''
                                echo "${topic}"
                            '''
                        }
            }
            stage ('Deploy') {
                sh "echo 'deploying to server ${config.serverDomain}...'"
            }
        } catch (err) {
            currentBuild.result = 'FAILED'
            throw err
        }
    }
}
