#!/bin/bash

source ../set_env.sh

cd $ES_INSTALL_LOCATION

echo "stopping kibana ...."
sudo kill `cat ./kibana/pid`
echo "kibana stopped!!!!"

