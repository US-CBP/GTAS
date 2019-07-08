#!/bin/bash
parent_dir=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
cd "$parent_dir"

sh ./neo4j/install.sh

sh ./pentaho/install.sh

# sh ./pentaho/config_pentaho.sh
sh ./neo4j/start_neo4j.sh