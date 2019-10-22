# FROM maven:3.3-alpine as maven

# WORKDIR /app
# COPY gtas-parent/pom.xml /app/pom.xml
# COPY gtas-parent/gtas-webapp/pom.xml /app/gtas-webapp/pom.xml
# COPY gtas-parent/gtas-commons/pom.xml /app/gtas-commons/pom.xml
# COPY gtas-parent/gtas-query-builder/pom.xml /app/gtas-query-builder/pom.xml
# COPY gtas-parent/gtas-rulesvc/pom.xml /app/gtas-rulesvc/pom.xml
# COPY gtas-parent/gtas-pdi-parser/pom.xml /app/gtas-pdi-parser/pom.xml
# COPY gtas-parent/gtas-job-scheduler-war/pom.xml /app/gtas-job-scheduler-war/pom.xml
# COPY gtas-parent/gtas-loader/pom.xml /app/gtas-loader/pom.xml
# COPY gtas-parent/gtas-parsers/pom.xml /app/gtas-parsers/pom.xml

# RUN mvn clean install dependency:go-offline '-Dmaven.exec.skip=true' -Dskip.unit.tests=true

# COPY ./gtas-parent/gtas-job-scheduler-war/src   /app/gtas-job-scheduler-war/src
# COPY ./gtas-parent/gtas-commons/src /app/gtas-commons/src
# COPY ./gtas-parent/gtas-rulesvc/src /app/gtas-rulesvc/src
# COPY ./gtas-parent/gtas-query-builder/src /app/gtas-query-builder/src
# COPY ./gtas-parent/gtas-loader/src /app/gtas-loader/src
# COPY ./gtas-parent/gtas-parsers/src /app/gtas-parsers/src

# ###

# RUN mvn clean install '-Dmaven.exec.skip=true' -Dskip.unit.tests=true --projects gtas-job-scheduler-war -am

FROM tomcat:9-jdk8-adoptopenjdk-openj9 as tomcat

ARG DB_HOST=mariahost
ARG DB_USERNAME=root
ARG DB_PASSWORD=admin

ENV DB_HOST=$DB_HOST
ENV DB_USERNAME=$DB_USERNAME
ENV DB_PASSWORD=$DB_PASSWORD

# COPY --from=maven /app/gtas-job-scheduler-war/target/gtas-job-scheduler.war /usr/local/tomcat/webapps/gtas-job-scheduler.war
# COPY --from=maven /app/gtas-job-scheduler-war/target/gtas-job-scheduler /usr/local/tomcat/webapps/gtas-job-scheduler 
# COPY ./docker/scheduler.properties /usr/local/tomcat/conf/application.properties
COPY target/gtas-job-scheduler.war /usr/local/tomcat/webapps/gtas-job-scheduler.war
COPY target/gtas-job-scheduler /usr/local/tomcat/webapps/gtas-job-scheduler 
#COPY ./docker/scheduler.properties /usr/local/tomcat/conf/application.properties
CMD ["catalina.sh", "run"]