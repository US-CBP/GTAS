#!/bin/bash


parent_dir=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd "$parent_dir"

# Set environment variable for elastic installation location and version
source ./set_env.sh

echo $ES_INSTALL_LOCATION/logstash/config

yes | cp -rf ../../config/logstash/* $ES_INSTALL_LOCATION/logstash/config
yes | cp -rf ../../config/logstash/mac/* $ES_INSTALL_LOCATION/logstash/config

wget https://downloads.mariadb.com/Connectors/java/connector-java-2.3.0/mariadb-java-client-2.3.0.jar -P  $ES_INSTALL_LOCATION/logstash/logstash-core/lib/jars
