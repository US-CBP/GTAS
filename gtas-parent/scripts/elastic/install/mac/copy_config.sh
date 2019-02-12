#!/bin/bash



cp -Rf ../../../config/* $ES_INSTALL_LOCATION/logstash/config

cp ../../../../../../gtas-parent/gtas-webapp/target/gtas/WEB-INF/lib/mariadb-java-client-2.3.0.jar $ES_INSTALL_LOCATION/logstash/config
