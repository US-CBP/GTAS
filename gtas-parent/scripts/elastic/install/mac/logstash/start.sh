#!/bin/bash

parent_dir=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd "$parent_dir"

source ../set_env.sh

cd $ES_INSTALL_LOCATION

cd "logstash"

# Start Elstic Search

./bin/logstash & echo $! > ./pid
