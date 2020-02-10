package com.jenkins.executors

class SonarqubeExecutor implements StageExecutor {
  def script
  def config

  SonarqubeExecutor(script, config) {
    this.script = script
    this.config = config
  }

  void execute() {
  }
}
