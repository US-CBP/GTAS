#!/bin/bash

parent_dir=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd "$parent_dir"

# The default working directory for logstash on linux installation is "/"
# All sql scripts, kibana templates and jar should be copied there.

LOGSTASH_WORKING_DIR="/"

echo $LOGSTASH_WORKING_DIR

yes | cp -rf ../../../config/logstash/*.conf $LOGSTASH_WORKING_DIR

yes | cp -rf ../../../config/logstash/*.sql $LOGSTASH_WORKING_DIR

yes | cp -rf ../../../config/logstash/*.json $LOGSTASH_WORKING_DIR

# yes | cp -rf ../../../config/logstash/*.yml $ES_INSTALL_LOCATION/

wget https://downloads.mariadb.com/Connectors/java/connector-java-2.3.0/mariadb-java-client-2.3.0.jar -P $LOGSTASH_WORKING_DIR
