#!/bin/bash


parent_dir=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd "$parent_dir"

# Set environment variable for elastic installation location and version
source ./set_env.sh

echo $ES_INSTALL_LOCATION/logstash/config

yes | cp -rf ../../config/logstash/* $ES_INSTALL_LOCATION/logstash/config
yes | cp -rf ../../config/logstash/mac/* $ES_INSTALL_LOCATION/logstash/config

yes | cp -f ../../../../../gtas-parent/gtas-webapp/target/gtas/WEB-INF/lib/mariadb-java-client-2.3.0.jar $ES_INSTALL_LOCATION/logstash/config
