#!/bin/bash
parent_dir=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
cd "$parent_dir"
# NEO4J_ETL_INSTALL_DIR=/gtas-neo4j-etl
source ../env.sh

sudo mkdir -p ${NEO4J_ETL_INSTALL_DIR}/{config,job,log,job/temp}

#Assign ownership
sudo chown -R $USER ${NEO4J_ETL_INSTALL_DIR}

#Give the right access to user and group of the user
sudo chmod -R 755 ${NEO4J_ETL_INSTALL_DIR}

# Copy the PDI jobs and transformations from the project
sudo cp ../../../../../gtas-neo4j-etl/job/*.ktr ${NEO4J_ETL_INSTALL_DIR}/job 
sudo cp ../../../../../gtas-neo4j-etl/job/*.kjb ${NEO4J_ETL_INSTALL_DIR}/job 

#  Copy the etl cofig folder to gtas-neo4j/config
sudo cp -r ../../../../../gtas-neo4j-etl/config/. ${NEO4J_ETL_INSTALL_DIR}/config
sudo chown -R $USER ${NEO4J_ETL_INSTALL_DIR}
sudo chmod -R 755 ${NEO4J_ETL_INSTALL_DIR}

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

APP_CONFIG="/gtas-neo4j-etl/config/application.properties"
PDI_DIR='\/pentaho\/data-integration\/.\/kitchen.sh'
sed -i.bak "/\(pdiDir.*=\).*/ s//\1${PDI_DIR}/" $APP_CONFIG

sudo chown -R $USER ${NEO4J_ETL_INSTALL_DIR}
sudo chmod -R 755 ${NEO4J_ETL_INSTALL_DIR}

sudo rm ${NEO4J_ETL_INSTALL_DIR}/config/*.bak