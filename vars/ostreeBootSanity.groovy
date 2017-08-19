import org.centos.jpipeline.Utils

def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def getUtils = new Utils()
    def current_stage = 'ostree-BootSanity'

    try {
        stage (current_stage) {
            env.branch = env.TARGET_BRANCH
            env.topic = "${MAIN_TOPIC}.package.complete"
            currentBuild.displayName = "Build#: ${env.BUILD_NUMBER} - Branch: ${env.branch}"
            currentBuild.description = "Stage: ${current_stage}"
            echo "Our main topic is ${env.MAIN_TOPIC}"
            sh '''
                echo "rpm build on branch ${TARGET_BRANCH} ..."
             '''
            //env.DUFFY_OPS = "--allocate"
            env.DUFFY_OPS = ""
            getUtils.duffyCciskel([stage:current_stage, duffyKey:'duffy-key', duffyOps:env.DUFFY_OPS])
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

            // step([$class: 'XUnitBuilder',
            //       thresholds: [[$class: 'FailedThreshold', unstableThreshold: '1']],
            //       tools: [[$class: 'JUnitType', pattern: "${env.ORIGIN_WORKSPACE}/logs/*.xml"]]]
            //)
        }
    } catch (err) {
        echo "Error: Exception from " + current_stage + ":"
        echo err.getMessage()
        throw err
    } finally {
        //env.DUFFY_OPS = "--teardown"
        env.DUFFY_OPS = ""
        getUtils.duffyCciskel([stage: current_stage, duffyKey: 'duffy-key', duffyOps: env.DUFFY_OP])

        env.messageProperties = "topic=${topic}\n" +
                "build_url=${BUILD_URL}\n" +
                "build_id=${BUILD_ID}\n" +
                "branch=${branch}\n" +
                "status=${currentBuild.currentResult}"
        env.MSG_PROPS = messageProperties
        env.CURRENT_STAGE = current_stage
    }
}