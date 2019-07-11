#!/bin/bash
parent_dir=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
cd "$parent_dir"

./pentaho/uninstall.sh
./neo4j/uninstall.sh