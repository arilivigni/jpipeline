// src/org/cipipeline/utils/Utils.groovy

package org.cipipeline.utils

class Utils implements Serializable {
    def steps

    Utils(steps) {this.steps = steps}
    def shell(command) {
        steps.sh "sh ${command}"
    }
}