#!/bin/bash
PARENT_DIR=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd "$PARENT_DIR"
source ../config.sh

#A directory where neo4j be installed

cd ${NEO4J_INSTALL_DIR}

chmod -R u+rw ${NEO4J_INSTALL_DIR}

#Download neo4j
wget http://dist.neo4j.org/neo4j-community-${NEO4J_VERSION}-unix.tar.gz
tar -xzf neo4j-community-${NEO4J_VERSION}-unix.tar.gz

chown -R $(whoami) ${NEO4J_INSTALL_DIR}/neo4j-community-${NEO4J_VERSION}
chmod -R 755 ${NEO4J_INSTALL_DIR}/neo4j-community-${NEO4J_VERSION}

rm -f neo4j-community-${NEO4J_VERSION}-unix.tar.gz

cd "$PARENT_DIR"

#Modifys the neo4j.config file with the expected parameters
CONFIG_FILE="/opt/neo4j-community-${NEO4J_VERSION}/conf/neo4j.conf"

echo "************Updating 'neo4j.conf'*************"

sed -i.bak '/#\(dbms.active_database=\).*/ s//\1gtas_db/' $CONFIG_FILE
sed -i.bak '/#\(dbms.connector.bolt.listen_address=:7687\)/ s//\1/' $CONFIG_FILE
sed -i.bak '/#\(dbms.connector.http.listen_address=:7474\)/ s//\1/' $CONFIG_FILE
sed -i.bak '/#\(dbms.security.auth_enabled=\)false/ s//\1true/' $CONFIG_FILE
sed -i.bak '/#\(dbms.connectors.default_advertised_address=localhost\)/ s//\1/' $CONFIG_FILE

rm ${NEO4J_INSTALL_DIR}/neco4j-community-${NEO4J_VERSION}/conf/*.bak

./config_etl_job.sh