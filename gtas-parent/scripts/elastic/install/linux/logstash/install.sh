#!/bin/bash

CURRENT_DIR=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd $CURRENT_DIR

../set_env.sh

# Install and start logstash

sudo yum install logstash-7.2.0 -y

systemctl daemon-reload

systemctl enable logstash

./copy_config.sh