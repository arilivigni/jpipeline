def allocDuffy(stage) {
    //echo "Currently in stage: ${stage} ${env.DUFFY_OP} resources"
    echo "Currently in stage: ${stage} resources"
    env.ORIGIN_WORKSPACE = "${env.WORKSPACE}/${stage}"
    env.ORIGIN_BUILD_TAG = "${env.BUILD_TAG}-${stage}"
    env.ORIGIN_CLASS = "builder"
    env.DUFFY_JOB_TIMEOUT_SECS = "3600"

    //withCredentials([file(credentialsId: 'duffy-key', variable: 'DUFFY_KEY')]) {
        sh '''
            #!/bin/bash
            set -xeuo pipefail
    
            echo "HELLO ${JOB_NAME}"
            exit
        '''
   // }
}

return this

