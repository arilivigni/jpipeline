def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    //node {
    // Clean workspace before doing anything
    // deleteDir()
    def getDuffy = new duffy()
    try {
        def currentStage = 'ci-pipeline-rpmbuild'
        stage (currentStage) {
            echo "Our main topic is ${config.mainTopic}"
            sh "echo 'rpmmbuild building on branch ${config.targetBranch} ...'"
            sh "echo 'Project Repo is ${config.projectRepo}...'"
            getDuffy.duffy(currentStage, "${config.duffyKey}", "${config.repoUrl}", "${config.subDir}", "${config.duffyOps}")
        }
    } catch (err) {
        currentBuild.result = 'FAILED'
        throw err
    }
    //}
}
