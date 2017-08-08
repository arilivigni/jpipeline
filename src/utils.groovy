// Convert bash shell properties to groovy

def convertProps(file1, file2) {
    def command = $/awk -F'=' '{print "env."$1"=\""$2"\""}' ${file1} > ${file2}/$
    sh command
}

// ensure we return 'this' on last line to allow this script to be loaded into flows
return this