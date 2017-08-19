def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def current_stage = ""
    try {
        rpmBuild {}
        ostreeCompose {}
    } catch (err) {
        echo err.getMessage()
        throw err
    } finally{
        //env.DUFFY_OPS = "--teardown"
        env.DUFFY_OPS = ""
        getUtils.duffyCciskel([stage: current_stage, duffyKey: 'duffy-key', duffyOps: env.DUFFY_OP])
    }
}