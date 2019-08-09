#!/bin/bash
parent_dir=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
cd "$parent_dir"

source ../env.sh
# Directory where pentaho will be installed
sudo mkdir -p /pentaho && sudo chown -R $USER /pentaho && sudo chmod -R 755 /pentaho

#Download pentaho
cd /pentaho
wget https://s3.amazonaws.com/kettle-neo4j/kettle-neo4j-remix-${PDI_VERSION}-REMIX.zip
unzip kettle-neo4j-remix-${PDI_VERSION}-REMIX.zip -d  /pentaho/
rm kettle-neo4j-remix-${PDI_VERSION}-REMIX.zip

cd "$parent_dir"
# Copy the MariaDB JDBC driver from the project
sudo cp ../../../../../gtas-neo4j-etl/drivers/mariadb-java-client-2.2.1.jar /pentaho/data-integration/lib
# Assign ownership and allow the user and the group to have the right access
sudo chown -R $USER /pentaho && sudo chmod -R 755 /pentaho

# Copy .pentaho folder and its contents to the users home directory
sudo cp -r ../../../../../gtas-neo4j-etl/pdi-conf/. ~/
sudo chown -R $USER ~/.pentaho && sudo chmod -R 755 ~/.pentaho
