#!/bin/bash

yum install wget unzip java-1.8.0-openjdk* -y

./logstash/install.sh

./elasticsearch/install.sh

./kibana/install.sh

