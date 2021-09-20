#!/bin/sh
set -e

TAG=${$1:-latest}

function deploy {
  echo "Deploy image to docker hub"
  docker push docker.io/saidsef/${PWD##*/}:$TAG
}
