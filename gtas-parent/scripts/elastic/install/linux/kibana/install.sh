#!/bin/bash

CURRENT_DIR=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd $CURRENT_DIR

../set_env.sh

# Install kabana

sudo yum install kibana-6.5.0-1 -y

systemctl daemon-reload

systemctl enable kibana

# Import gtas default dashboard

../../../config/kibana/kibana.import-dashboard.sh