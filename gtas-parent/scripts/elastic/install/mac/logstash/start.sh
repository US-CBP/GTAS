#!/bin/bash

parent_dir=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd "$parent_dir"

source ../set_env.sh

cd $ES_INSTALL_LOCATION

cd "logstash"

export ELASTICSEARCH_HOSTS='https://localhost:9200'
export SERVER_HOST='localhost'
export DATABASE_HOST='localhost'
export LOGSTASH_DIR=$ES_INSTALL_LOCATION/logstash
export LOGSTASH_LIB=$LOGSTASH_DIR/
export MARIADB_USER='root'
export MARIADB_HOST='localhost'
export MARIADB_PASSWORD='admin'

export ES_PASSWORD=
# Start Elstic Search

./bin/logstash & echo $! > ./pid
