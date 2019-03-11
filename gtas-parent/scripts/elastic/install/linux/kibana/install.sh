#!/bin/bash

cp ../../elastic_stack_yum_repos.repo /etc/yum.repos.d/elastic_stack.repo

sudo yum install elasticsearch -y

systemctl daemon-reload

systemctl enable elasticsearch

systemctl start elasticsearch

# Install and start kabana

sudo yum install kibana -y

systemctl daemon-reload

systemctl enable kibana

systemctl start kibana

# Install and start logstash

sudo yum install logstash -y

systemctl daemon-reload

systemctl enable logstashipt

systemctl start logstash
