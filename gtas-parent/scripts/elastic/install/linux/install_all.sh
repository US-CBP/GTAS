#!/bin/bash

sudo yum install git wget unzip -y

./logstash/install.sh

./elasticsearch/install.sh

./kibana/install.sh

./setup-security.sh

