import org.centos.jpipeline.Utils

def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def getUtils = new Utils()
    def current_stage = 'rpmbuild'

    try {
        stage (current_stage) {
            echo "Our main topic is ${env.MAIN_TOPIC}"
            sh '''
                echo "rpm build on branch ${TARGET_BRANCH} ..."
             '''
            //env.DUFFY_OPS = "--allocate"
            env.DUFFY_OPS = ""
            getUtils.duffyCciskel([stage:current_stage, duffyKey:'duffy-key', duffyOps:env.DUFFY_OPS])
            env.branch = env.TARGET_BRANCH
            env.topic = "${MAIN_TOPIC}.package.complete"
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
            echo "TOPIC: ${env.topic}"
            echo "BRANCH: ${env.TARGET_BRANCH}"
        }
    } catch (err) {
        echo "Error: Exception from " + current_stage + ":"
        echo err.getMessage()
        throw err
    } finally {
        //env.DUFFY_OPS = "--teardown"
        env.DUFFY_OPS = ""
        getUtils.duffyCciskel([stage:current_stage, duffyKey:'duffy-key', duffyOps:env.DUFFY_OP])
    }
}