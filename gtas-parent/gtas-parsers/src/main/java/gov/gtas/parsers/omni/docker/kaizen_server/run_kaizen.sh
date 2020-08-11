#!/bin/bash
echo "Starting Kaizen Server"
# source ~/.bash_profile
java -Dserver.port=8082 -jar /opt/omni/model-manager-1.0.0.jar
