#! /bin/bash

/opt/omni/livy/bin/livy-server start

while sleep 5; do
  if [[ ! $(/opt/omni/livy/bin/livy-server status | grep pid) ]]; then
    exit
  fi
done
