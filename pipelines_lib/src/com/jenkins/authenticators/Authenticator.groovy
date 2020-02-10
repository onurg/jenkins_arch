package com.jenkins.authenticators

interface Authenticator extends java.io.Serializable {
  void authenticate()
}
