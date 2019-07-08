#!/bin/bash
parent_dir=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

PDI_VERSION=8.2.0.3-519

# Directory where pentaho will be installed
sudo mkdir -p /pentaho && sudo chown -R $USER /pentaho && sudo chmod -R 755 /pentaho

#Download pentaho
cd /pentaho
wget https://s3.amazonaws.com/kettle-neo4j/kettle-neo4j-remix-${PDI_VERSION}-REMIX.zip
unzip kettle-neo4j-remix-${PDI_VERSION}-REMIX.zip -d  /pentaho/
rm kettle-neo4j-remix-${PDI_VERSION}-REMIX.zip

cd "$parent_dir"
sh ./config_pentaho.sh
