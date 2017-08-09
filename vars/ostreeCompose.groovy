import org.centos.*

def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def getDuffy = new duffy()

    try {
        def current_stage = 'ci-pipeline-ostree-compose'
        stage (current_stage) {
            echo "Our main topic is ${env.MAIN_TOPIC}"
            sh "echo 'ostree compose on branch ${env.TARGET_BRANCH} ...'"
            sh "echo 'Project Repo is ${env.PROJECT_REPO}...'"
            env.DUFFY_OPS = "--allocate"
            getDuffy.duffy(current_stage, "${env.DUFFY_OPS}")
            env.DUFFY_OPS = "--teardown"
            getDuffy.duffy(current_stage, "${env.DUFFY_OPS}")

        }
    } catch (err) {
        echo "Error: Exception from " + current_stage + ":"
        echo e.getMessage()
        throw err
    }
}