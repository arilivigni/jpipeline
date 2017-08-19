import org.centos.jpipeline.Utils

def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def current_stage = ""
    def getUtils = new Utils()

    try {
        rpmBuild {}
        sh 'sleep 10'
        ostreeCompose {}
        ostreeBootSanity {}
    } catch (err) {
        echo err.getMessage()
        throw err
    }
}