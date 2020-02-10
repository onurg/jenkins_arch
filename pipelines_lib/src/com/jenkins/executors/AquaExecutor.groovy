package com.jenkins.executors

class AquaExecutor implements StageExecutor {
  def script
  def config

  AquaExecutor(script, config) {
    this.script = script
    this.config = config
  }

  void execute() {
    script.withCredentials([script.usernamePassword(credentialsId: 'aqua-scanner', 
      passwordVariable: 'AQUA_PW', usernameVariable: 'AQUA_USERNAME')]) {
        script.aqua hideBase: false, 
        hostedImage: '', 
        localImage: config.image, 
        locationType: 'local', 
        notCompliesCmd: 'exit 1', 
        onDisallowed: 'fail', 
        policies: '', 
        register: false, 
        registry: '', 
        showNegligible: true
    }
  }
}
