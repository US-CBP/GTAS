#!/bin/bash

parent_dir=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd "$parent_dir"

# Set environment variable for elastic installation location and version
source ../set_env.sh

cd $ES_INSTALL_LOCATION

# Download Kibana
wget https://artifacts.elastic.co/downloads/kibana/kibana-${ES_INSTALL_VERSION}-darwin-x86_64.tar.gz


sudo -u $USER tar xvf kibana-${ES_INSTALL_VERSION}-darwin-x86_64.tar.gz

# Soft link elasticsearch -> elasticsearch-2.3.2, this way when the elasticsearch ES_INSTALL_VERSION is updated, we would only need to update the tomcat link
ln -s kibana-${ES_INSTALL_VERSION}-darwin-x86_64 kibana

rm -f kibana-${ES_INSTALL_VERSION}-darwin-x86_64.tar.gz

