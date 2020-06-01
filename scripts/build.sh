#!/bin/sh
set -e

TAG=$1

function usage {
  echo """
    USAGE:
    ./build.sh <label>
  """
}

function build {
  echo "Build container"
  docker build --build-arg "TAG=${TAG}" -t saidsef/${PWD##*/}:${TAG} .
}

function push {
  echo "Pushing image to docker hub"
  docker push saidsef/${PWD##*/}:${TAG}
  echo $?
}

function main {
  if [ -z "${TAG}" ]; then
    usage
  else
    build
    push
  fi
}

main
