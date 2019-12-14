FROM adoptopenjdk/maven-openjdk8 as build-stage

COPY ./docker-resources/building-script.sh /building-script.sh
COPY ./docker-resources/hibernate.properties /hibernate.properties
COPY ./ /gtas-parent/

RUN apt-get -y update && apt-get -y install nodejs && apt-get -y install git && sh /building-script.sh



FROM tomcat:9-jdk8-adoptopenjdk-openj9 as tomcat

COPY --from=build-stage /root/.m2/repository/gov/gtas/gtas-webapp/1.0.0-BUILD-SNAPSHOT/gtas-webapp-1.0.0-BUILD-SNAPSHOT.war /gtas-webapp-1.0.0-BUILD-SNAPSHOT.war
COPY ./docker-resources/default.application.properties /usr/local/tomcat/conf/application.properties
COPY ./docker-resources/setenv.sh /usr/local/tomcat/bin/setenv.sh
COPY ./entrypoint.sh /entrypoint.sh

ENTRYPOINT sh /entrypoint.sh  