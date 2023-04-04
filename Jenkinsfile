#!/usr/bin/env groovy

/**
 * Sample Jenkinsfile for Jenkins2 Pipeline
 * from https://github.com/saidsef
 * by saidsef@gmail.com
 */

pipeline {
  agent any
  options {
    disableConcurrentBuilds()
    timestamps()
  }
  environment {
    TAG = "${new Date().format("Y.M")}"
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
        script {
          def app = docker.build("docker.io/saidsef/alpine-jenkins-dockerfile:${env.BUILD_NUMBER}", ".")
          /**
          * In order to configure the registry credentials, go the Jenkins Manager Credentials page.
          * Add a new username/password entry and enter your registry login and password.
          */
          app.withRegistry("https://registry.hub.docker.com", "dockerhub")
          app.push("docker.io/saidsef/alpine-jenkins-dockerfile:${env.BUILD_NUMBER}")
        }
      }
    }
  }
  post {
    success {
      deleteDir()
    }
  }
}
