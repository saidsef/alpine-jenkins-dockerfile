#!/bin/sh

set -ex

main() {
  runTest
}

runTest() {
  response=$(curl -s -w %{http_code} -o /dev/null http://localhost:8080/)
  echo $response
  if [ $response -ne 200 ]
  then
    echo "fail!"
    exit 100
  fi
}

main
