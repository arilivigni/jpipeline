import org.centos.jpipeline.Utils

def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def getDuffy = new Utils()

    try {
        def current_stage = 'ci-pipeline-ostree-compose'
        stage (current_stage) {
            echo "Our main topic is ${env.MAIN_TOPIC}"
            sh "echo 'ostree compose on branch ${env.TARGET_BRANCH} ...'"
            sh "echo 'Project Repo is ${env.PROJECT_REPO}...'"
            env.DUFFY_OPS = "--allocate"
            getDuffy.duffyCciskel(current_stage, "${env.DUFFY_OPS}")
            env.DUFFY_OPS = "--teardown"
            getDuffy.duffyCciskel(current_stage, "${env.DUFFY_OPS}")

        }
    } catch (err) {
        echo "Error: Exception from " + current_stage + ":"
        echo err.getMessage()
        throw err
    }
}