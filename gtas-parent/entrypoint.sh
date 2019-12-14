#!/bin/bash

export DB_USERNAME=$(cat /run/secrets/mysql_webapp_user)
export DB_PASSWORD=$(cat /run/secrets/mysql_webapp_password)

cp /gtas-webapp-1.0.0-BUILD-SNAPSHOT.war /usr/local/tomcat/webapps/gtas.war

mkdir /usr/local/tomcat/webapps/gtas
cp /default.application.properties /usr/local/tomcat/conf/application.properties
cd /usr/local/tomcat/webapps/gtas && jar -xvf /usr/local/tomcat/webapps/gtas.war

rm -f /usr/local/tomcat/webapps/gtas/resources/bower_components && cp -R /usr/local/tomcat/webapps/gtas/resources/node_modules/@bower_components /usr/local/tomcat/webapps/gtas/resources/bower_components

/usr/local/tomcat/bin/catalina.sh run