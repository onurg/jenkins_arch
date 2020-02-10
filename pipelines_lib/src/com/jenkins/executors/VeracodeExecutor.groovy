package com.jenkins.executors

class VeracodeExecutor implements StageExecutor {
  def script
  def config

  VeracodeExecutor(script, config) {
    this.script = script
    this.config = config
  }

  void execute() {
    script.withCredentials([script.usernamePassword(credentialsId: 'veracode-scanner', 
        passwordVariable: 'VERACODE_PW', usernameVariable: 'VERACODE_USERNAME')]) { 
        script.veracode applicationName: config.appName, 
        canFailJob: (config.namespace == 'dev') ? false : true, 
        criticality: 'VeryHigh', 
        debug: true,
        // pHost: 'proxy.pfshq.com', 
        // pPort: '3128', 
        // pUser: 'devopsvc',  
        // pPassword: 'xxx', 
        fileNamePattern: '', 
        replacementPattern: '', 
        sandboxName: '', 
        scanExcludesPattern: '', 
        scanIncludesPattern: '', 
        scanName: "${config.scanName}.${config.buildNumber}", 
        teams: '',
        timeout: 60, 
        uploadExcludesPattern: '', 
        uploadIncludesPattern: config.scannedFiles, 
        useIDkey: true, 
        vid: script.VERACODE_USERNAME, 
        vkey: script.VERACODE_PW, 
        vpassword: '', 
        vuser: '', 
        waitForScan: (config.namespace == 'dev') ? false : true
    }
  }
}
