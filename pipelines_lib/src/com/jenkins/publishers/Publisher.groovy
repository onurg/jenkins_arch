package com.jenkins.publishers

import java.io.Serializable

interface Publisher extends Serializable {
  void publish()
}
