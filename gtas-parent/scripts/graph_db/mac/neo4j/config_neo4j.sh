#!/bin/bash

#Modifys the neo4j.config file with the expected parameters
CONFIG_FILE="/usr/local/bin/neo4j-community-3.5.3/conf/neo4j.conf"

echo "************Updating 'neo4j.conf'*************"

sed -i.bak '/#\(dbms.active_database=\).*/ s//\1gtas_db/' $CONFIG_FILE
sed -i.bak '/#\(dbms.connector.bolt.listen_address=:7687\)/ s//\1/' $CONFIG_FILE
sed -i.bak '/#\(dbms.connector.http.listen_address=:7474\)/ s//\1/' $CONFIG_FILE
sed -i.bak '/#\(dbms.security.auth_enabled=\)false/ s//\1true/' $CONFIG_FILE
sed -i.bak '/#\(dbms.connectors.default_advertised_address=localhost\)/ s//\1/' $CONFIG_FILE

sudo rm /usr/local/bin/neo4j-community-3.5.3/conf/*.bak