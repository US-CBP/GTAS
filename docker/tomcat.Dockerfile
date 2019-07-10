FROM tomcat

COPY ./gtas-parent/gtas-webapp/target/gtas.war /usr/local/tomcat/webapps/gtas.war
COPY ./gtas-parent/gtas-job-scheduler-war/target/gtas-job-scheduler.war /usr/local/tomcat/webapps/gtas-job-scheduler.war
COPY ./gtas-parent/gtas-configs/servers/tomcat/context.xml /usr/local/tomcat/conf/context.xml
COPY ./gtas-parent/gtas-configs/servers/tomcat/server.xml /usr/local/tomcat/conf/server.xml

RUN mkdir /usr/local/input && mkdir /usr/local/output

CMD ["catalina.sh", "run"]