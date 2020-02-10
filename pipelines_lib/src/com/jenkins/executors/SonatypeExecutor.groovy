package com.jenkins.executors

class SonatypeExecutor implements StageExecutor {
  def script
  def config

  SonatypeExecutor(script, config) {
    this.script = script
    this.config = config
  }

  void execute() { 
  	script.withCredentials([script.usernamePassword(credentialsId: 'nexusIQ', 
      passwordVariable: 'SONATYPE_PW', usernameVariable: 'SONATYPE_USERNAME')]) {
    	  script.nexusPolicyEvaluation iqApplication: 'config.sonatypeIqApp',
    	  iqStage: 'build',
    	  jobCredentialsId: 'nexusIQ'
    }
  }
}