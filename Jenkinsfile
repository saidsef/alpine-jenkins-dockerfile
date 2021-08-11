#!/usr/bin/env groovy

/**
 * Sample Jenkinsfile for Jenkins2 Pipeline
 * from https://github.com/saidsef
 * by saidsef@gmail.com
 */

def now = new Date()

pipeline {
  agent any
  options {
    skipStagesAfterUnstable()
    disableConcurrentBuilds()
    timestamps()
  }
  environment {
    TAG = "${now.format("Y.M")}"
  }
  stages {
    stage("Checkout") {
      steps {
        deleteDir()
        checkout scm
      }
    }
    stage("Build and push container") {
      steps {
        def app = docker.build("saidsef/alpine-jenkins-dockerfile:${env.BUILD_NUMBER}", ".")
        /**
        * In order to configure the registry credentials, go the Jenkins Manager Credentials page.
        * Add a new username/password entry and enter your registry login and password.
        */
        app.withRegistry("https://registry.hub.docker.com", "dockerhub")
        app.push("saidsef/alpine-jenkins-dockerfile:${env.BUILD_NUMBER}")
      }
    }
    post {
      success {
        deleteDir()
      }
    }
  }
}
