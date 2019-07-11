#!/bin/bash
parent_dir=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
cd "$parent_dir"

source ../config.sh

mkdir -p ${NEO4J_ETL_INSTALL_DIR}/{config,job,log,job/temp}

#Assign ownership
chown -R $(whoami) ${NEO4J_ETL_INSTALL_DIR} 

#Give the right access to user and group of the user
chmod -R 755 ${NEO4J_ETL_INSTALL_DIR}

# Copy the PDI jobs and transformations from the project
cp ../../../../../gtas-neo4j-etl/job/*.ktr ${NEO4J_ETL_INSTALL_DIR}/job 
cp ../../../../../gtas-neo4j-etl/job/*.kjb ${NEO4J_ETL_INSTALL_DIR}/job 

#  Copy the etl cofig folder to gtas-neo4j/config
cp -r ../../../../../gtas-neo4j-etl/config/. ${NEO4J_ETL_INSTALL_DIR}/config
# sudo chown -R $USER ${NEO4J_ETL_INSTALL_DIR}
# sudo chmod -R 755 ${NEO4J_ETL_INSTALL_DIR}

#Edit gtas-neo4j-config.properties

CONFIG_FILE=${NEO4J_ETL_INSTALL_DIR}"/config/gtas-neo4j-config.properties"
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

rm ${NEO4J_ETL_INSTALL_DIR}/config/*.bak
