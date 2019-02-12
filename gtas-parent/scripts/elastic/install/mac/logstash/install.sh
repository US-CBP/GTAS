#!/bin/bash

parent_dir=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd "$parent_dir"

# Set environment variable for elastic installation location and version
source ../set_env.sh

export CURRENT_DIR="$PWD"

echo $CURRENT_DIR

cd $ES_INSTALL_LOCATION

# Download Logstash

wget https://artifacts.elastic.co/downloads/logstash/logstash-${ES_INSTALL_VERSION}.tar.gz


sudo -u $USER tar xvf logstash-${ES_INSTALL_VERSION}.tar.gz

# Soft link elasticsearch -> elasticsearch-2.3.2, this way when the elasticsearch ES_INSTALL_VERSION is updated, we would only need to update the tomcat link
ln -s logstash-${ES_INSTALL_VERSION} logstash

rm -f logstash-${ES_INSTALL_VERSION}.tar.gz

cd $CURRENT_DIR

cd "$parent_dir"

source ../copy_config.sh