#!/bin/bash
parent_dir=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
# Directory where pentaho will be installed

cd "$parent_dir"
source ../config.sh

# Directory where pentaho will be installed
 mkdir -p ${INSTALL_DIR} &&  chown -R $(whoami) ${INSTALL_DIR} &&  chmod -R 755 ${INSTALL_DIR}

#Download pentaho
cd ${INSTALL_DIR}
wget https://s3.amazonaws.com/kettle-neo4j/kettle-neo4j-remix-${PDI_VERSION}-REMIX.zip
unzip kettle-neo4j-remix-${PDI_VERSION}-REMIX.zip -d  ${INSTALL_DIR}/
rm kettle-neo4j-remix-${PDI_VERSION}-REMIX.zip

cd "$parent_dir"

# Copy the MariaDB JDBC driver from the project
 cp ../../../../../gtas-neo4j-etl/drivers/mariadb-java-client-2.2.1.jar ${INSTALL_DIR}/data-integration/lib
# Assign ownership and allow the user and the group to have the right access
 chown -R $(whoami) ${INSTALL_DIR} &&  chmod -R 755 ${INSTALL_DIR}

# Copy .pentaho folder and its contents to the users home directory
 cp -r ../../../../../gtas-neo4j-etl/pdi-conf/. ~/
 chown -R $(whoami) ~/.pentaho &&  chmod -R 755 ~/.pentaho
