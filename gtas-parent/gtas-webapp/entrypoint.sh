#!/bin/bash

VERSION_NUMBER=1.0.0
BUILD_NUMBER=SNAPSHOT
WEBAPP_WAR="/root/.m2/repository/gov/gtas/gtas-webapp/$VERSION_NUMBER-BUILD-$BUILD_NUMBER/gtas-webapp-$VERSION_NUMBER-BUILD-$BUILD_NUMBER.war"
RETRY_IN_SECONDS=10

WEBAPP_WAR_EXISTS=false

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

cp /root/.m2/repository/gov/gtas/gtas-webapp/1.0.0-BUILD-SNAPSHOT/gtas-webapp-1.0.0-BUILD-SNAPSHOT.war /usr/local/tomcat/webapps/gtas.war

mkdir /usr/local/tomcat/webapps/gtas

cd /usr/local/tomcat/webapps/gtas && jar -xvf /usr/local/tomcat/webapps/gtas.war

rm -f /usr/local/tomcat/webapps/gtas/resources/bower_components && cp -R /usr/local/tomcat/webapps/gtas/resources/node_modules/@bower_components /usr/local/tomcat/webapps/gtas/resources/bower_components

/usr/local/tomcat/bin/catalina.sh run