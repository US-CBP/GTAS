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

cd "$parent_dir"
yes | cp -f ../../../config/kibana/kibana.yml $ES_INSTALL_LOCATION/kibana/config

# Copy Certificates
mkdir -p $ES_INSTALL_LOCATION/kibana/config/certs
yes | cp -f $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/docker-cluster/kibana/kibana.crt $ES_INSTALL_LOCATION/kibana/config/certs
yes | cp -f $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/docker-cluster/kibana/kibana.key $ES_INSTALL_LOCATION/kibana/config/certs
yes | cp -f $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/ca/ca.crt $ES_INSTALL_LOCATION/kibana/config/certs
