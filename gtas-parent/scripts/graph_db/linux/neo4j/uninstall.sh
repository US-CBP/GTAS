#!/bin/bash
parent_dir=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
cd "$parent_dir"

./stop_neo4j.sh
rm -rf /opt/neo4j-community-3.5.3
sudo rm -rf /gtas-neo4j-etl