#!/bin/bash

CURRENT_DIR=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd $CURRENT_DIR

../set_env.sh

# Install and start logstash

sudo yum install logstash-6.5.0 -y

systemctl daemon-reload

systemctl enable logstash