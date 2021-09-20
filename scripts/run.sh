#!/bin/sh
set -ex

TAG=${$1:-latest}
BASE=${$2:-$PWD}

function usage {
  echo """
  USAGE:
  ./run.sh <container-label>
  """
}

function run {
  echo "Create Jenkins CI/CD spaces"
  mkdir -p $BASE/jenkins/{workspace,jobs,cache,logs,secrets}

  echo "Run Jenkins CI/CD Server"
  docker run -p 8080:8080 \
  -v $BASE/jenkins/workspace:/var/jenkins_home/workspace \
  -v $BASE/jenkins/jobs:/var/jenkins_home/jobs \
  -v $BASE/jenkins/cache:/var/jenkins_home/cache \
  -v $BASE/jenkins/logs:/var/jenkins_home/jenkins-jobs \
  -v $BASE/jenkins/secrets:/var/jenkins_home/secrets \
  docker.io/saidsef/${PWD##*/}:${TAG}
}

function main {
  if [ -z "${TAG}" ]; then
    usage
  else
    run
  fi
}

main
