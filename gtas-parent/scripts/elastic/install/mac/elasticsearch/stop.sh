#!/bin/bash

source ../set_env.sh

cd $ES_INSTALL_LOCATION

echo "stopping elastic search ...."
sudo kill `cat ./elasticsearch/pid`
echo "elastic search stopped!!!!"

