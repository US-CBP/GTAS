FROM tomcat:8-jre8

COPY gtas-webapp/target/gtas.war /usr/local/tomcat/webapps/
COPY gtas-job-scheduler-war/target/gtas-job-scheduler.war /usr/local/tomcat/webapps/

RUN mkdir /usr/local/input && mkdir /usr/local/output

CMD ["catalina.sh", "run"]
