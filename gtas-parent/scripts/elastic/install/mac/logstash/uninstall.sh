#!/bin/bash

parent_dir=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd "$parent_dir"

source ./stop.sh


rm -rf $ES_INSTALL_LOCATION/logstash*
