package com.jenkins.executors

import java.io.Serializable

interface StageExecutor extends Serializable {
  void execute()
}
