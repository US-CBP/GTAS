#!/bin/bash

CURRENT_DIR=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd $CURRENT_DIR

../set_env.sh

sudo yum install elasticsearch-6.5.0-1 -y

systemctl daemon-reload

systemctl enable elasticsearch

# Import flightpax and case indices

../../../config/elasticsearch/update_template.sh