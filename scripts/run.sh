#!/bin/sh
set -e

BUILD_ID=$1

function usage {
  echo """
  USAGE:
  ./run.sh <container-label>
  """
}

function run {
  echo "Create Jenkins CI/CD spaces"
  mkdir -p /mnt/jenkins/{workspace,jobs,cache,logs,secrets}

  echo "Update permissions"
  chmod 777 -R /mnt/jenkins/{workspace,jobs,cache,logs,secrets}

  echo "Run Jenkins CI/CD"
  docker run -d -p 9099:8080 \
  -v /mnt/jenkins/workspace:/var/jenkins_home/workspace \
  -v /mnt/jenkins/jobs:/var/jenkins_home/jobs \
  -v /mnt/jenkins/cache:/var/jenkins_home/cache \
  -v /mnt/jenkins/logs:/var/jenkins_home/jenkins-jobs \
  -v /mnt/jenkins/secrets:/var/jenkins_home/secrets \
  saidsef/alpine-jenkins-dockerfile:${BUILD_ID}
}

function main {
  if [ -z "${BUILD_ID}" ]; then
    usage
  else
    run
  fi
}

main
