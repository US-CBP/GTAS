#!/bin/bash
cp /hibernate.properties /gtas-parent/gtas-commons/src/main/resources/hibernate.properties
cd /gtas-parent
mvn clean install -Dmaven.test.skip=true