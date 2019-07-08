#!/bin/bash
parent_dir=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
cd "$parent_dir"

sudo mkdir -p /gtas-neo4j-etl/{config,job,log,job/temp}

#Assign ownership
sudo chown -R $USER /gtas-neo4j-etl 

#Give the right access to user and group of the user
sudo chmod -R 755 /gtas-neo4j-etl

# Copy the PDI jobs and transformations from the project
sudo cp ../../../../../gtas-neo4j-etl/job/*.ktr /gtas-neo4j-etl/job 
sudo cp ../../../../../gtas-neo4j-etl/job/*.kjb /gtas-neo4j-etl/job 

#  Copy the etl cofig folder to gtas-neo4j/config
sudo cp -r ../../../../../gtas-neo4j-etl/config/. /gtas-neo4j-etl/config
sudo chown -R $USER /gtas-neo4j-etl
sudo chmod -R 755 /gtas-neo4j-etl

#Edit gtas-neo4j-config.properties

CONFIG_FILE="/gtas-neo4j-etl/config/gtas-neo4j-config.properties"
# change the user name accordingly 
GTAS_DB_USER_NAME=root
NEO4J_USER_NAME=neo4j
# change the user password accordingly 
GTAS_DB_PASSWORD=admin
NEO4J_PASSWORD=admin

# edit the gtas db (mariadb) user name and password to the config file
sed -i.bak "/\(EXT_VAR_GTAS_DB_USER_NAME.*=\).*/ s//\1${GTAS_DB_USER_NAME}/" $CONFIG_FILE
sed -i.bak "/\(EXT_VAR_GTAS_DB_PASSWORD.*=\).*/ s//\1${GTAS_DB_PASSWORD}/" $CONFIG_FILE

# Edit the neo4j user name and password
sed -i.bak "/\(EXT_VAR_NEO4J_DB_USER_NAME.*=\).*/ s//\1${NEO4J_USER_NAME}/" $CONFIG_FILE
sed -i.bak "/\(EXT_VAR_NEO4J_DB_PASSWORD.*=\).*/ s//\1${NEO4J_PASSWORD}/" $CONFIG_FILE

sudo rm /gtas-neo4j-etl/config/*.bak
