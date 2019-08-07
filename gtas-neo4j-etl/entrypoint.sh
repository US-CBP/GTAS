#!/bin/bash

TARGET="/root/.m2/repository/gov/gtas/gtas-neo4j-job-scheduler/1/gtas-neo4j-job-scheduler-1.jar"
RETRY_IN_SECONDS=10

TARGET_EXISTS=false

while [ "$TARGET_EXISTS" != "true" ] 
do
    if test -f "$TARGET"; then
        TARGET_EXISTS=true;
        echo " gtas-neo4j-job-scheduler-1.jar is ready for deployment"
    else
        echo "$TARGET not found. retrying in $RETRY_IN_SECONDS seconds";
        sleep $RETRY_IN_SECONDS;
    fi
done

cp /root/.m2/repository/gov/gtas/gtas-neo4j-job-scheduler/1/gtas-neo4j-job-scheduler-1.jar /gtas-neo4j-etl
java -jar /gtas-neo4j-etl/gtas-neo4j-job-scheduler-1.jar