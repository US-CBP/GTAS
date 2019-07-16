FROM maven:3.3-alpine as maven

WORKDIR /app
COPY gtas-parent/pom.xml /app/pom.xml
COPY gtas-parent/gtas-webapp/pom.xml /app/gtas-webapp/pom.xml
COPY gtas-parent/gtas-commons/pom.xml /app/gtas-commons/pom.xml
COPY gtas-parent/gtas-query-builder/pom.xml /app/gtas-query-builder/pom.xml
COPY gtas-parent/gtas-rulesvc/pom.xml /app/gtas-rulesvc/pom.xml
COPY gtas-parent/gtas-pdi-parser/pom.xml /app/gtas-pdi-parser/pom.xml
COPY gtas-parent/gtas-job-scheduler-war/pom.xml /app/gtas-job-scheduler-war/pom.xml
COPY gtas-parent/gtas-loader/pom.xml /app/gtas-loader/pom.xml
COPY gtas-parent/gtas-parsers/pom.xml /app/gtas-parsers/pom.xml

RUN mvn dependency:resolve-plugins dependency:resolve install '-Dmaven.exec.skip=true' -Dskip.unit.tests=true --projects gtas-webapp -am

COPY ./gtas-parent/gtas-webapp/src /app/gtas-webapp/src
COPY ./gtas-parent/gtas-commons/src /app/gtas-commons/src
COPY ./gtas-parent/gtas-rulesvc/src /app/gtas-rulesvc/src
COPY ./gtas-parent/gtas-query-builder/src /app/gtas-query-builder/src

###

RUN mvn clean package '-Dmaven.exec.skip=true' -Dskip.unit.tests=true --projects gtas-webapp -am

FROM node:12-alpine as node
WORKDIR /app/gtas-webapp/src/main/webapp/resources
COPY --from=maven  /app/gtas-webapp/src/main/webapp/resources /app/gtas-webapp/src/main/webapp/resources
RUN yarn install --silent

FROM tomcat:9-jdk8-adoptopenjdk-openj9 as tomcat
COPY --from=maven /app/gtas-webapp/target/gtas.war /usr/local/tomcat/webapps/gtas.war
COPY --from=maven /app/gtas-webapp/target/gtas /usr/local/tomcat/webapps/gtas 
COPY ./docker/webapp.properties /usr/local/tomcat/conf/application.properties
COPY --from=node /app/gtas-webapp/src/main/webapp/resources/ /usr/local/tomcat/webapps/gtas/resources/
RUN unlink /usr/local/tomcat/webapps/gtas/resources/bower_components && cp -R /usr/local/tomcat/webapps/gtas/resources/node_modules/@bower_components /usr/local/tomcat/webapps/gtas/resources/bower_components
EXPOSE 8080

CMD ["catalina.sh", "run"]