#!/bin/bash

source ../set_env.sh

cd $ES_INSTALL_LOCATION

# Start Elstic Search

./logstash/bin/logstash & echo $! > ./logstash/pid
