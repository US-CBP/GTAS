#!/bin/bash
PARENT_DIR=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
NEO4J_VERSION=3.5.3
#A directory where neo4j be installed
cd /opt

#Download neo4j
wget http://dist.neo4j.org/neo4j-community-${NEO4J_VERSION}-unix.tar.gz
sudo tar -xzf neo4j-community-${NEO4J_VERSION}-unix.tar.gz

sudo chown -R $USER /opt/neo4j-community-3.5.3
sudo chmod -R 755 /opt/neo4j-community-3.5.3

rm -f neo4j-community-${NEO4J_VERSION}-unix.tar.gz

cd "$PARENT_DIR"

sh ./config_neo4j.sh
sh ./config_etl_job.sh