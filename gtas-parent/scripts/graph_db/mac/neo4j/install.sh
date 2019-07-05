#!/bin/bash
PARENT_DIR=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

#A directory where neo4j be installed
cd /usr/local/bin/

#Download neo4j
wget http://dist.neo4j.org/neo4j-community-3.5.3-unix.tar.gz
sudo -u $USER tar xvf neo4j-community-3.5.3-unix.tar.gz

rm -f neo4j-community-3.5.3-unix.tar.gz

cd "$PARENT_DIR"

sh ./config_neo4j.sh
sh ./config_etl_job.sh
