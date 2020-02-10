package com.jenkins.pipelines

abstract class AbstractPipeline {

  boolean init() {
    chatNotify("Build started: ${script.env.JOB_NAME} #${script.env.BUILD_NUMBER}")
    banner()
    buildProperties()

    if (isBranchIndexingCause()) {
      script.echo "Aborting due to branch indexing"
      script.currentBuild.result = "ABORTED"
      return false
    }

    return true
  }

  void banner() {}

  boolean isBranchIndexingCause() {
    def isBranchIndexing = false
    script.currentBuild.rawBuild.getCauses().each { cause ->
      if (cause instanceof jenkins.branch.BranchIndexingCause) {
        isBranchIndexing = true
      }
    }
    return isBranchIndexing
  }

  void buildProperties() {

    def props = [
      // only keep last 5 builds
      [ $class: 'BuildDiscarderProperty',
        strategy: [
          $class: 'LogRotator', daysToKeepStr: '10', numToKeepStr: '5'
        ]
      ],
      script.pipelineTriggers([
        [$class: 'GitHubPushTrigger']
      ]),
      script.durabilityHint('PERFORMANCE_OPTIMIZED')
    ]

    script.properties(props)
  }

  void chatNotify(msg) {}

  void buildDescription(msg) {
    script.currentBuild.description = "<b style='font-size: 1.05rem'>$msg</b>"
  }
}
