import org.centos.jpipelin.Utils

def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def getUtils = new Utils()

    try {
        def current_stage = 'ci-pipeline-ostree-compose'
        stage (current_stage) {
            echo "Our main topic is ${env.MAIN_TOPIC}"
            sh "echo 'ostreeCompose on branch ${env.TARGET_BRANCH} ...'"
            env.DUFFY_OPS = "--allocate"
            getUtils.duffyCciskel([stage:current_stage, duffyKey:'duffy-key', duffyOps:env.DUFFY_OP])
            env.branch = env.TARGET_BRANCH
            env.topic = "${MAIN_TOPIC}.compose.complete"
            sh '''
                echo "branch=${branch}" > ${WORKSPACE}/job.properties
                echo "topic=${topic}" >> ${WORKSPACE}/job.properties
            '''
            def job_props = "${env.WORKSPACE}/job.properties"
            def job_props_groovy = getUtils.convertProps(job_props)
            load(job_props_groovy)
            sh '''
                cat ${WORKSPACE}/job.properties
                cat ${WORKSPACE}/job.properties.groovy
            '''
            env.DUFFY_OPS = "--teardown"
            echo "TOPIC: ${env.topic}"
            echo "BRANCH: ${env.TARGET_BRANCH}"
            getUtils.duffyCciskel([stage:current_stage, duffyKey:'duffy-key', duffyOps:env.DUFFY_OP])
        }
    } catch (err) {
        echo "Error: Exception from " + current_stage + ":"
        echo err.getMessage()
        throw err
    }
}