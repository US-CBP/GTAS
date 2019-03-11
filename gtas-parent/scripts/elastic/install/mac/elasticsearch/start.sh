#!/bin/bash

parent_dir=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd "$parent_dir"

source ../set_env.sh

cd $ES_INSTALL_LOCATION

# Start Elstic Search

./elasticsearch/bin/elasticsearch & echo $! >  ./elasticsearch/pid
