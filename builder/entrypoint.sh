#!/bin/bash

VERSION_NUMBER=1.0.0
BUILD_NUMBER=SNAPSHOT
WEBAPP_WAR="//root/.m2/repository/gov/gtas/gtas-webapp/$VERSION_NUMBER-BUILD-$BUILD_NUMBER/gtas-webapp-$VERSION_NUMBER-BUILD-$BUILD_NUMBER.war"

if test -f "$WEBAPP_WAR"; then
	cat /gtas-parent/gtas-commons/src/main/resources/default.application.properties
	echo "gtas-webapp-$VERSION_NUMBER-BUILD-$BUILD_NUMBER.war already exists"
else
	cp default.application.properties /gtas-parent/gtas-commons/src/main/resources/default.application.properties
	cp hibernate.properties /gtas-parent/gtas-commons/src/main/resources/hibernate.properties
	cd gtas-parent
	mvn clean install -Dmaven.test.skip=true
	cd gtas-commons
	mvn hibernate:create
fi