FROM adoptopenjdk/maven-openjdk8 as scheduler-builder


COPY ./gtas-parent/ /gtas-parent
WORKDIR /gtas-parent
RUN mvn clean install -Dmaven.test.skip=true --projects gtas-job-scheduler-war -am

RUN apt-get -y update && apt-get -y install dos2unix
RUN mkdir /temp-dos
COPY ./gtas-parent/docker-resources/setenv-scheduler.sh /temp-dos/setenv-scheduler.sh
RUN dos2unix /temp-dos/setenv-scheduler.sh

FROM tomcat:9-jdk8-adoptopenjdk-openj9 as tomcat


COPY --from=scheduler-builder /root/.m2/repository/gov/gtas/gtas-job-scheduler-war/1.0.0-BUILD-SNAPSHOT/gtas-job-scheduler-war-1.0.0-BUILD-SNAPSHOT.war /usr/local/tomcat/webapps/gtas-job-scheduler.war
COPY ./gtas-parent/docker-resources/default.application.properties /usr/local/tomcat/conf/application.properties
COPY --from=scheduler-builder /temp-dos/setenv-scheduler.sh /usr/local/tomcat/bin/setenv.sh

RUN apt-get -y update && apt-get -y install wget
RUN wget https://github.com/jwilder/dockerize/releases/download/v0.6.1/dockerize-linux-amd64-v0.6.1.tar.gz
RUN tar -C /usr/local/bin -xvzf dockerize-linux-amd64-v0.6.1.tar.gz


ENTRYPOINT mkdir -p /usr/local/gtas-data/processed /usr/local/gtas-data/error /usr/local/gtas-data/input /usr/local/gtas-data/working && dockerize -wait tcp://${DB_HOST}:3306 -timeout 1000s catalina.sh run