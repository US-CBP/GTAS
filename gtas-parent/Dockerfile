FROM adoptopenjdk/maven-openjdk8 as build-stage

RUN apt-get -y update && apt-get -y install nodejs && apt-get -y install git && apt-get -y install dos2unix

COPY ./ /gtas-parent/
COPY ./docker-resources/hibernate.properties /gtas-parent/gtas-commons/src/main/resources/hibernate.properties

WORKDIR /gtas-parent
RUN mvn clean install -Dmaven.test.skip=true
RUN cd / && rm -rf /gtas-parent

RUN mkdir /temp-dos
COPY ./docker-resources/setenv.sh /temp-dos/setenv.sh
RUN dos2unix /temp-dos/setenv.sh


FROM tomcat:9-jdk8-adoptopenjdk-openj9 as tomcat

RUN mkdir /usr/local/tomcat/webapps/gtas

COPY --from=build-stage /root/.m2/repository/gov/gtas/gtas-webapp/1.0.0-BUILD-SNAPSHOT/gtas-webapp-1.0.0-BUILD-SNAPSHOT.war /usr/local/tomcat/webapps/gtas.war
COPY ./docker-resources/default.application.properties /usr/local/tomcat/conf/application.properties
COPY --from=build-stage /temp-dos/setenv.sh /usr/local/tomcat/bin/setenv.sh


RUN apt-get -y update && apt-get install -y logrotate && apt-get install -y rename
COPY ./docker-resources/logrotate.conf /
RUN mkdir -p /logs/apache-tomcat-web && mkdir -p /logs/apache-tomcat && mkdir /temp


WORKDIR /usr/local/tomcat/webapps/gtas

RUN  jar -xvf /usr/local/tomcat/webapps/gtas.war

WORKDIR /usr/local/tomcat/bin

ENTRYPOINT mkdir -p /scheduler-logs/temp && logrotate /logrotate.conf && catalina.sh run