#!/bin/bash

parent_dir=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd "$parent_dir"

source ../set_env.sh

cd $ES_INSTALL_LOCATION

echo "stopping elastic search ...."
sudo kill -9 `cat ./elasticsearch/pid`
echo "elastic search stopped!!!!"

