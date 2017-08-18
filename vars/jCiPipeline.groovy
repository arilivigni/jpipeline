def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    try {
        rpmBuild {}
        ostreeCompose {}
    } catch (err) {
        echo err.getMessage()
        throw err
    }
}