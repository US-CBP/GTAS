#!/bin/bash

VERSION_NUMBER=1.0.0
BUILD_NUMBER=SNAPSHOT
WEBAPP_WAR="/root/.m2/repository/gov/gtas/gtas-webapp/$VERSION_NUMBER-BUILD-$BUILD_NUMBER/gtas-webapp-$VERSION_NUMBER-BUILD-$BUILD_NUMBER.war"
RETRY_IN_SECONDS=10

WEBAPP_WAR_EXISTS=false

#Waiting for the WAR file to get mounted to m2 volume by builder script
while [ "$WEBAPP_WAR_EXISTS" != "true" ] 
do
    if test -f "$WEBAPP_WAR"; then
        WEBAPP_WAR_EXISTS=true;
        echo "gtas-webapp-$VERSION_NUMBER-BUILD-$BUILD_NUMBER.war is ready for deployment"
    else
        echo "$WEBAPP_WAR not found. retrying in $RETRY_IN_SECONDS seconds";
        sleep $RETRY_IN_SECONDS;
    fi
done

# Waiting for the builder to finish populating the database
if test $IS_DOCKER_RUN && $IS_DOCKER_RUN == "true"; then
	BUILDER_COMPLETE=false;
	while ["$BUILDER_COMPLETE" != "true"]
	do
		STATUS=$(cat service_status/builder_status.txt)
		if $STATUS == "completed"; then
			BUILDER_COMPLETE=true;
		else
        	echo "Builder not completed yet; retrying in $RETRY_IN_SECONDS seconds";
        	sleep $RETRY_IN_SECONDS;
    	fi
	done
fi


cp /root/.m2/repository/gov/gtas/gtas-webapp/1.0.0-BUILD-SNAPSHOT/gtas-webapp-1.0.0-BUILD-SNAPSHOT.war /usr/local/tomcat/webapps/gtas.war

mkdir /usr/local/tomcat/webapps/gtas

cd /usr/local/tomcat/webapps/gtas && jar -xvf /usr/local/tomcat/webapps/gtas.war

rm -f /usr/local/tomcat/webapps/gtas/resources/bower_components && cp -R /usr/local/tomcat/webapps/gtas/resources/node_modules/@bower_components /usr/local/tomcat/webapps/gtas/resources/bower_components

/usr/local/tomcat/bin/catalina.sh run