#!/bin/sh
set -e

TAG=$1
BASE=$HOME

function usage {
  echo """
  USAGE:
  ./run.sh <container-label>
  """
}

function run {
  echo "Create Jenkins CI/CD spaces"
  mkdir -p $BASE/jenkins/{workspace,jobs,cache,logs,secrets}

  echo "Update permissions"
  chmod 777 -R $BASE/jenkins/{workspace,jobs,cache,logs,secrets}

  echo "Run Jenkins CI/CD"
  docker run -d -p 9099:8080 \
  -v $BASE/jenkins/workspace:/var/jenkins_home/workspace \
  -v $BASE/jenkins/jobs:/var/jenkins_home/jobs \
  -v $BASE/jenkins/cache:/var/jenkins_home/cache \
  -v $BASE/jenkins/logs:/var/jenkins_home/jenkins-jobs \
  -v $BASE/jenkins/secrets:/var/jenkins_home/secrets \
  docker.io/saidsef/alpine-jenkins-dockerfile:${TAG}
}

function main {
  if [ -z "${TAG}" ]; then
    usage
  else
    run
  fi
}

main
