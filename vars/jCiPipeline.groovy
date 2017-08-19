import org.centos.jpipeline.Utils

def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def current_stage = ""
    def getUtils = new Utils()

    try {
        // SCM
        dir('jpipeline') {
            git 'https://github.com/arilivigni/jpipeline'
        }

        rpmBuild {}
        sh 'sleep 3'
        ostreeCompose {}
        sh 'sleep 3'
        ostreeBootSanity {}
    } catch (err) {
        echo err.getMessage()
        throw err
    }
}