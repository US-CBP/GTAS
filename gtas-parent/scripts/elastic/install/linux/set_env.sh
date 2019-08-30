#!/bin/bash

CURRENT_DIR=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd $CURRENT_DIR

cp ./elasticsearch/elasticsearch.repo /etc/yum.repos.d/elasticsearch.repo
cp ./kibana/kibana.repo /etc/yum.repos.d/kibana.repo
cp ./logstash/logstash.repo /etc/yum.repos.d/logstash.repo
