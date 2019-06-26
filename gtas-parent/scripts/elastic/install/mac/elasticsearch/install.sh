#!/bin/bash

parent_dir=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd "$parent_dir"

# Set environment variable for elastic installation location and version
source ../set_env.sh

cd $ES_INSTALL_LOCATION

# Download Elasticsearch
wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-${ES_INSTALL_VERSION}.tar.gz

sudo -u $USER tar xvf elasticsearch-${ES_INSTALL_VERSION}.tar.gz

# Soft link elasticsearch -> elasticsearch-2.3.2, when elasticsearch is updated, we only need to update the link to point to the current version installed
ln -s elasticsearch-${ES_INSTALL_VERSION} elasticsearch

# delete the installation file
rm -f elasticsearch-${ES_INSTALL_VERSION}.tar.gz
