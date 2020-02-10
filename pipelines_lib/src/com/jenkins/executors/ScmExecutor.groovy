package com.jenkins.executors

class ScmExecutor implements StageExecutor {
  def script
  def config
  def ws
  def version
  def remoteUrl
  def commitHash
  def repoName

  ScmExecutor(script, config) {
    this.script = script
    this.config = config
  }

  void execute() {
    remoteUrl = script.scm.getUserRemoteConfigs()[0].getUrl()
    repoName = remoteUrl.replace('https://github.com', '').replace('.git', '').replaceFirst('/', '')
    remoteUrl = remoteUrl.replace('https://github.com/', 'git@github.com:')

    def res
    if (config.commitHash) {
      res = script.checkout scm: [
        $class: 'GitSCM',
        userRemoteConfigs: [[url: remoteUrl, credentialsId: 'github-ssh-key']],
        branches: [[name: config.commitHash]]
      ], changelog: false, poll: false
    } else {
      res = script.checkout script.scm
    }
    
    commitHash = res["GIT_COMMIT"]

    if (config.versionCommand) {
      version = script.sh script: config.versionCommand, returnStdout: true
      version = version.trim()
    }
  }
}
