package com.jenkins.executors

import java.util.List

class DockerExecutor implements StageExecutor {
  def script
  def config
  def images = []

  DockerExecutor(script, config) {
    this.script = script
    this.config = config
  }

  void execute() {
    if (config.registryAuthenticator)
      config.registryAuthenticator.authenticate()
    
    script.withCredentials([script.usernamePassword(credentialsId: '9900da86-e25f-47bc-9d50-5e2424d04e16',
        usernameVariable: 'DCKR_USR', passwordVariable: 'DCKR_PW')]) {
      script.sh """
        docker system prune -f
        docker build --no-cache -t ${config.appName}:${config.commitHash} .
        """    
    }
  }
}