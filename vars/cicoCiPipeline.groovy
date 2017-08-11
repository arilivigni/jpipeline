import org.centos.jpipeline.Utils

def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def getDuffy = new Utils()
    try {
        def current_stage = 'cico-pipeline-lib-stage1'
        //stage(current_stage) {
        stage('cico-pipeline-lib-stage1') {
            //getDuffy.duffyCciskel(current_stage)
            getDuffy.duffyCciskel('cico-pipeline-lib-stage1')
        }
        current_stage = 'cico-pipeline-lib-stage2'
        stage(current_stage) {
            //getDuffy.duffyCciskel(current_stage)
            getDuffy.duffyCciskel('cico-pipeline-lib-stage1')
        }
    } catch (err) {
        //echo "Error: Exception from " + current_stage + ":"
        echo e.getMessage()
        throw err
    }
}