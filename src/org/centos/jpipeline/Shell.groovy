#!/usr/bin/groovy
package org.centos.jpipeline

def pipe(command){
    sh(script: command, returnStdout: true)
}

return this