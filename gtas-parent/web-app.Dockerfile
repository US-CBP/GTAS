FROM adoptopenjdk/maven-openjdk8 as build-stage

RUN apt-get -y update && apt-get -y install nodejs git dos2unix

COPY ./ /gtas-parent/
COPY ./docker-resources/hibernate.properties /gtas-parent/gtas-commons/src/main/resources/hibernate.properties

WORKDIR /gtas-parent
RUN mvn clean install -Dmaven.test.skip=true
RUN cd / && rm -rf /gtas-parent

RUN mkdir /temp-dos
COPY ./docker-resources/setenv.sh /temp-dos/setenv.sh
RUN dos2unix /temp-dos/setenv.sh


FROM tomcat:9-jdk8-adoptopenjdk-openj9 as tomcat

RUN mkdir -p /usr/local/tomcat/webapps/gtas /logs/apache-tomcat-web /logs/apache-tomcat /temp

COPY --from=build-stage /root/.m2/repository/gov/gtas/gtas-webapp/1.0.0-BUILD-SNAPSHOT/gtas-webapp-1.0.0-BUILD-SNAPSHOT.war /usr/local/tomcat/webapps/gtas.war
COPY --from=build-stage /temp-dos/setenv.sh /usr/local/tomcat/bin/setenv.sh
COPY ./docker-resources/default.application.properties /usr/local/tomcat/conf/application.properties
COPY ./docker-resources/logrotate.conf /
COPY ./docker-resources/server.xml /usr/local/tomcat/conf/

RUN apt-get -y update && apt-get install -y logrotate wget
RUN wget https://github.com/jwilder/dockerize/releases/download/v0.6.1/dockerize-linux-amd64-v0.6.1.tar.gz
RUN tar -C /usr/local/bin -xzf dockerize-linux-amd64-v0.6.1.tar.gz

WORKDIR /usr/local/tomcat/webapps/gtas
RUN  jar -xvf /usr/local/tomcat/webapps/gtas.war

WORKDIR /usr/local/tomcat/bin
ENTRYPOINT mkdir -p /scheduler-logs/temp && dockerize -wait tcp://${DB_HOST}:3306 -timeout 1000s logrotate /logrotate.conf && catalina.sh run