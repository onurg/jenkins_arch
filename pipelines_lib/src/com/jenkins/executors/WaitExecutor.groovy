package com.jenkins.executors

class WaitExecutor implements StageExecutor {
  def script
  def config

  WaitExecutor(script, config) {
    this.script = script
    this.config = config
  }

  void execute() {
    script.timeout(config.timeoutMins) {
      script.waitUntil {
        def ret = script.sh script: config.command, returnStatus: true
        return (ret == 0)
      }
    }
  }
}

