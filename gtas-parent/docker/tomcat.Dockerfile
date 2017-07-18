FROM tomcat:8-jre8
# COPY gtas-parent/gtas-webapp/target/gtas.war /usr/local/tomcat/webapps/
# COPY gtas-parent/gtas-job-scheduler-war/target/gtas-job-scheduler.war /usr/local/tomcat/webapps/
CMD ["catalina.sh", "run"]
