def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def getDuffy = new duffy()

    try {
        def currentStage = 'ci-pipeline-rpmbuild'
        stage (currentStage) {
            echo "Our main topic is ${config.mainTopic}"
            sh "echo 'rpmmbuild building on branch ${config.targetBranch} ...'"
            sh "echo 'Project Repo is ${config.projectRepo}...'"
            env.DUFFY_OPS = "--allocate"
            getDuffy.duffy(currentStage, "${env.DUFFY_OPS}")
            env.DUFFY_OPS = "--teardown"
            getDuffy.duffy(currentStage, "${env.DUFFY_OPS}")

        }
        currentStage = 'ci-pipeline-ostree-compose'
        stage (currentStage) {
            echo "Our main topic is ${config.mainTopic}"
            sh "echo 'rpmmbuild building on branch ${config.targetBranch} ...'"
            sh "echo 'Project Repo is ${config.projectRepo}...'"
            env.DUFFY_OPS = "--allocate"
            getDuffy.duffy(currentStage, "${env.DUFFY_OPS}")
            env.DUFFY_OPS = "--teardown"
            getDuffy.duffy(currentStage, "${env.DUFFY_OPS}")
        }
    } catch (err) {
        currentBuild.result = 'FAILED'
        throw err
    }
}