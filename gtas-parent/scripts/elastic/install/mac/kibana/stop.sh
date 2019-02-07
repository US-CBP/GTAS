#!/bin/bash
parent_dir=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd "$parent_dir"

source ../set_env.sh

cd $ES_INSTALL_LOCATION

echo "stopping kibana ...."
sudo kill -9 `cat ./kibana/pid`
echo "kibana stopped!!!!"

