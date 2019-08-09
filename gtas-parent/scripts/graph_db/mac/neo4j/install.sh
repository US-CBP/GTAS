#!/bin/bash
PARENT_DIR=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
cd "$PARENT_DIR"
#A directory where neo4j be installed
source ../env.sh
# cd /usr/local/bin/
cd $NEO4J_INSTALL_DIR

# NEO4J_VERISON=3.5.3
#Download neo4j
wget http://dist.neo4j.org/neo4j-community-${NEO4J_VERISON}-unix.tar.gz
sudo -u $USER tar xvf neo4j-community-${NEO4J_VERISON}-unix.tar.gz

rm -f neo4j-community-${NEO4J_VERISON}-unix.tar.gz

cd "$PARENT_DIR"

./config_neo4j.sh
./config_etl_job.sh
