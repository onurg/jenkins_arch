package com.jenkins.publishers

class HtmlPublisher implements Publisher {
  def script
  def config

  HtmlPublisher(script, config) {
    this.script = script
    this.config = config
  }

  void publish() {
    script.publishHTML(target: [
      allowMissing: config.allowMissing ?: false,
      alwaysLinkToLastBuild: config.alwaysLinkToLastBuild ?: false,
      keepAll: config.keepAll ?: true,
      reportDir: config.reportDir,
      reportFiles: config.reportFiles,
      reportTitles: config.reportTitles ?: null,
      reportName: config.reportName
    ])
  }
}
