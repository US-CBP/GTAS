FROM adoptopenjdk/maven-openjdk8 as scheduler-builder


COPY ./gtas-parent/ /gtas-parent
WORKDIR /gtas-parent
RUN mvn clean install -Dmaven.test.skip=true --projects gtas-job-scheduler-war -am


FROM tomcat:9-jdk8-adoptopenjdk-openj9 as tomcat


COPY --from=scheduler-builder /root/.m2/repository/gov/gtas/gtas-job-scheduler-war/1.0.0-BUILD-SNAPSHOT/gtas-job-scheduler-war-1.0.0-BUILD-SNAPSHOT.war /usr/local/tomcat/webapps/gtas-job-scheduler.war
COPY ./gtas-parent/docker-resources/default.application.properties /usr/local/tomcat/conf/application.properties
COPY ./gtas-parent/docker-resources/setenv-scheduler.sh /usr/local/tomcat/bin/setenv.sh

CMD ["catalina.sh", "run"]