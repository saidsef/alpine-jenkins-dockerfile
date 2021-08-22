#!/bin/sh
set -ex

TAG=$1

function deploy {
  echo "Deploy image to docker hub"
  docker push saidsef/alpine-jenkins-dockerfile:$TAG
}
