#!/bin/bash

source ../set_env.sh

cd $ES_INSTALL_LOCATION

echo "stopping logstash ...."
sudo kill `cat ./logstash/pid`
echo "logstash stopped!!!!"

