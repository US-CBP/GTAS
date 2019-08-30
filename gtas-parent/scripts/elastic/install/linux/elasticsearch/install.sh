#!/bin/bash

CURRENT_DIR=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd $CURRENT_DIR

../set_env.sh

sudo yum install elasticsearch-7.2.0-1 -y

yes | cp -f ../../../config/elasticsearch/elasticsearch.yml /etc/elasticsearch
yes | cp -f ../../../config/elasticsearch/log4j2.properties /etc/elasticsearch

systemctl daemon-reload

systemctl enable elasticsearch

# Import flightpax and case indices

# ../../../config/elasticsearch/update_template.sh