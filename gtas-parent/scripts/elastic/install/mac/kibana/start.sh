#!/bin/bash

source ../set_env.sh

cd $ES_INSTALL_LOCATION

# Start Elstic Search

./kibana/bin/kibana & echo $! > ./kibana/pid
