#!/bin/bash

source ../set_env.sh

cd $ES_INSTALL_LOCATION

# Start Elstic Search

./elasticsearch/bin/elasticsearch & echo $! >  ./elasticsearch/pid
