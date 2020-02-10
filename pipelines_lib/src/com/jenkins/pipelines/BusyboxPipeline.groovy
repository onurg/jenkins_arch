package com.jenkins.pipelines

import groovy.json.*
import java.io.PrintWriter
import java.io.StringWriter
import com.jenkins.executors.*

class BusyboxPipeline extends AbstractPipeline implements Pipeline {
  def script
  def config

  BusyboxPipeline(script, config) {
    this.script = script
    this.config = config
  }

  void execute() {
    // timestamp all console output
    script.timestamps {
      script.node("busybox") {
        try {
            def env = script.env
            def envCredentials = null
            def envName = env.JOB_NAME.split('/')[1]
            boolean requireApproval = (envName == 'dev') ? false : true

            if (!init()) {
                // init failed, abort
                return
            }
            
            def checkoutExecutor = new ScmExecutor(script, [
              commitHash: script.params.COMMIT_HASH
            ])
            script.executeStage('Checkout', checkoutExecutor)

            // build the docker image
            def buildExecutor = new DockerExecutor(script, [
              appName: config.appName
              commitHash: checkoutExecutor.commitHash
            ])
            script.executeStage('Build&Push', buildExecutor)
          
            script.executeStage('Deploy') {
              if (requireApproval) {
                def approved = false
                // wait for input
                try {
                  def timedout = true
                  script.timeout(10) {
                    script.input message: "Deploy to ${namespace} environment?", ok: 'Deploy'
                    approved = true
                    timedout = false
                  }
                  if (timedout) {
                    throw new Exception("Timed out waiting for approval")
                  } else {
                    script.echo "Aborted by the User"
                  }
                } catch (err) {
                  approved = false
                  script.currentBuild.result = 'ABORTED'
                  return
                }
              }
            }
            // send chat notification
            script.echo("Build complete: ${env.JOB_NAME} #${env.BUILD_NUMBER}")
        } catch (err) {
            script.echo "Unexpected exception: $err"
            script.currentBuild.result = 'FAILURE'
        } finally {
            script.step([$class: 'WsCleanup'])
        }
      }
    }
  }
}
