#!/bin/sh
set -e

BUILD_ID=$1

function usage {
  echo """
    USAGE:
    ./build.sh <label>
  """
}

function build {
  echo "Build container"
  docker build --build-arg "BUILD_ID=${BUILD_ID}" -t saidsef/${PWD##*/}:${BUILD_ID} .
}

function push {
  echo "Pushing image to docker hub"
  docker push saidsef/${PWD##*/}:${BUILD_ID}
  echo $?
}

function main {
  if [ -z "$1" ]; then
    usage
  else
    build
    push
  fi
}

main
