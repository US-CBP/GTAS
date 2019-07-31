#!/bin/bash
parent_dir=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
cd "$parent_dir"

cd ../../../../gtas-neo4j-scheduler

# #Create the target file for neo4j job scheduler
mvn clean install 


cp ./target/gtas-neo4j-job-scheduler-1.jar /gtas-neo4j-etl/
sudo chmod 755 /gtas-neo4j-etl/gtas-neo4j-job-scheduler-1.jar

# echo "***********Starting the scheduler***********"
cd /gtas-neo4j-etl
java -jar gtas-neo4j-job-scheduler-1.jar 