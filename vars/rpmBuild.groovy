import org.centos.jpipeline.Utils

def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def getProps = new Utils()

    try {
        def current_stage = 'ci-pipeline-rpmbuild'
        stage (current_stage) {
            echo "Our main topic is ${env.MAIN_TOPIC}"
            sh "echo 'rpmmbuild building on branch ${env.TARGET_BRANCH} ...'"
            sh "echo 'Project Repo is ${env.PROJECT_REPO}...'"
            env.DUFFY_OPS = "--allocate"
            getDuffy.duffyCciskel(current_stage, "${env.DUFFY_OPS}")
            sh '''
                echo "branch=${TARGET_BRANCH}" > ${WORKSPACE}/job.properties
                echo "topic=${MAIN_TOPIC}.package.complete" >> ${WORKSPACE}/job.properties
            '''
            def job_props = "${env.WORKSPACE}/job.properties"
            def job_props_groovy = "${env.WORKSPACE}/job.properties.groovy"
            getProps.convertProps(job_props, job_props_groovy)
            sh '''
                cat ${WORKSPACE}/job.properties
                cat ${WORKSPACE}/job.properties.groovy
            '''
            load(job_props_groovy)
            env.DUFFY_OPS = "--teardown"
            getDuffy.duffyCciskel(current_stage, "${env.DUFFY_OPS}")

        }
    } catch (err) {
        echo "Error: Exception from " + current_stage + ":"
        echo err.getMessage()
        throw err
    }
}