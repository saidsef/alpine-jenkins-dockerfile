#!/usr/bin/env groovy

/**
 * Sample Jenkinsfile for Jenkins2 Pipeline
 * from https://github.com/saidsef
 * by saidsef@gmail.com
 */

import hudson.model.*
import hudson.EnvVars
import groovy.json.JsonSlurperClassic
import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import java.net.URL

node {
  try {
    currentBuild.result = "SUCCESS"
    stage("Checkout") {
      deleteDir()
      checkout scm
    }
    stage("Build and test container") {
      def app = docker.build("jenkins:jenkins-build-${env.BUILD_NUMBER}", ".")
      app.push("jenkins-build-${env.BUILD_NUMBER}")
    }
  } catch (err) {
    currentBuild.result = "FAILURE"
    throw err
  } finally {
    /**
    * Clean up build directory
    */
    deleteDir()
  }
}
