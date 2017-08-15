import org.centos.jpipeline.Utils

def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def getUtils = new Utils()
    def current_stage = 'cico-pipeline-lib-stage'
    def duffyCciskelOps = [:]
    try {
        current_stage = 'cico-pipeline-lib-stage1'
        duffyCciskelOps = [stage:current_stage]
        stage(current_stage) {
            getUtils.duffyCciskel(duffyCciskelOps)
        }
        current_stage = 'cico-pipeline-lib-stage2'
        duffyCciskelOps = [stage:current_stage]
        stage(current_stage) {
            getUtils.duffyCciskel(duffyCciskelOps)
        }
    } catch (err) {
        echo "Error: Exception from " + current_stage + ":"
        echo err.getMessage()
        throw err
    }
}