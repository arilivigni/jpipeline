// vars/rpmBuild.groovy

class rpmBuild implements Serializable {

    private String name
    def setName(value) {
        name = value
    }
    def getName() {
        name
    }
    def caution(message) {
        echo "Hello, ${name}! CAUTION: ${message}"
    }
}