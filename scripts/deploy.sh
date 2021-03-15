#!/bin/sh
set -ex

function deploy {
  echo "Deploy image to docker hub"
  docker push saidsef/alpine-jenkins-dockerfile:$TRAVIS_BRANCH
}