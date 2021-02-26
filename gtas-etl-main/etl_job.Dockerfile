FROM adoptopenjdk/maven-openjdk8 as scheduler-builder

COPY ./gtas-etl-scheduler /etl-project
WORKDIR /etl-project
RUN mvn clean install -Dskip.unit.tests=false



FROM alpine as pentaho-extractor

ENV PDI_VERSION=8.2.0.3-519 
RUN apk update && apk add wget
RUN mkdir /opt/pentaho
RUN wget https://s3.amazonaws.com/kettle-neo4j/kettle-neo4j-remix-${PDI_VERSION}-REMIX.zip \
    -O /tmp/kettle-neo4j-remix-${PDI_VERSION}-REMIX.zip
RUN /usr/bin/unzip -q /tmp/kettle-neo4j-remix-${PDI_VERSION}-REMIX.zip -d  /opt/pentaho
RUN rm /tmp/kettle-neo4j-remix-${PDI_VERSION}-REMIX.zip



# FROM java:8-jre
FROM openjdk:8-jre-alpine

ENV GTAS_ETL_HOME=/gtas-etl \
    NEO4J_ETL_HOME=/gtas-etl/job/neo4j-etl \
	NEO4J_ETL_CONFIG_FILE=/gtas-etl/config/neo4j-etl/job.properties \
    REPORT_ETL_HOME=/gtas-etl/job/report-etl \
    REPORT_ETL_CONFIG_FILE=/gtas-etl/config/report-etl/job.properties

RUN mkdir -p  ${GTAS_ETL_HOME}/config && \
    mkdir -p  ${GTAS_ETL_HOME}/config/neo4j-etl && \
	mkdir -p  ${GTAS_ETL_HOME}/config/report-etl && \
	mkdir -p  ${GTAS_ETL_HOME}/config/scheduler && \
    mkdir -p  ${GTAS_ETL_HOME}/job && \
    mkdir -p  ${GTAS_ETL_HOME}/job/neo4j-etl && \
    mkdir -p  ${GTAS_ETL_HOME}/job/report-etl && \
	mkdir -p  ${GTAS_ETL_HOME}/log && \
	mkdir -p  ${GTAS_ETL_HOME}/log/neo4j-etl && \
	mkdir -p  ${GTAS_ETL_HOME}/log/report-etl && \
	mkdir -p  ${GTAS_ETL_HOME}/log/scheduler 
    
RUN apk add --no-cache bash 
COPY --from=scheduler-builder /root/.m2/repository/gov/gtas/gtas-etl-scheduler/1/gtas-etl-scheduler-1.jar /gtas-etl

# Install pentaho
COPY --from=pentaho-extractor /opt/pentaho/ /opt/pentaho/
COPY ./gtas-etl/driver/mariadb-java-client-2.2.1.jar /opt/pentaho/data-integration/lib/

# copy .pnetaho to user's home directory 
COPY ./gtas-etl/pdi-conf/ /root

# etl job configs 
COPY ./gtas-etl-main/job/neo4j-etl ${NEO4J_ETL_HOME}
COPY ./gtas-etl-main/config/neo4j-etl ${GTAS_ETL_HOME}/config/neo4j-etl
COPY ./gtas-etl-main/job/report-etl ${REPORT_ETL_HOME}
COPY ./gtas-etl-main/config/report-etl ${GTAS_ETL_HOME}/config/report-etl
COPY ./gtas-etl-main/config/scheduler ${GTAS_ETL_HOME}/config/scheduler

WORKDIR ${GTAS_ETL_HOME}/

RUN apk add dos2unix
RUN dos2unix config/neo4j-etl/job.properties
RUN dos2unix config/neo4j-etl/mask-run-record.properties
RUN dos2unix config/neo4j-etl/run-record.properties
RUN dos2unix config/report-etl/job.properties
RUN dos2unix config/scheduler/scheduler.properties
RUN dos2unix job/report-etl/index/index.properties
RUN dos2unix job/report-etl/template/template.properties
RUN dos2unix job/report-etl/workpad/workpad.properties


ENTRYPOINT export NEO4J_USER_NAME=$(cat ${NEO4J_USER_PATH}) NEO4J_PASSWORD=$(cat ${NEO4J_PASSWORD_PATH}) && \
    export GTAS_DB_USER_NAME=$(cat ${MYSQL_USER_PATH}) GTAS_DB_PASSWORD=$(cat ${MYSQL_PASSWORD_PATH}) && \
    export GTAS_DB_REPORT_USER_NAME=$(cat ${ MYSQL_REPORT_USER_PATH}) GTAS_DB_REPORT_USER_PASSWORD=$(cat ${MYSQL_REPORT_USER_PASSWORD_PATH}) && \
    export ELASTICSEARCH_USER_NAME=$(cat ${ELASTICSEARCH_USER_PATH}) ELASTICSEARCH_USER_PASSWORD=$(cat ${ELASTICSEARCH_USER_PASSWORD_PATH}) && \
    export KIBANA_USER_NAME=$(cat ${KIBANA_USER_PATH}) KIBANA_USER_PASSWORD=$(cat ${KIBANA_USER_PASSWORD_PATH}) && \
    sed -i.bak "/\(EXT_VAR_GTAS_DB_USER_NAME.*=\).*/ s//\1${GTAS_DB_USER_NAME}/" $NEO4J_ETL_CONFIG_FILE && \
    sed -i.bak "/\(EXT_VAR_GTAS_DB_PASSWORD.*=\).*/ s//\1${GTAS_DB_PASSWORD}/" $NEO4J_ETL_CONFIG_FILE && \
    sed -i.bak "/\(EXT_VAR_NEO4J_DB_USER_NAME.*=\).*/ s//\1${NEO4J_USER_NAME}/" $NEO4J_ETL_CONFIG_FILE && \
    sed -i.bak "/\(EXT_VAR_NEO4J_DB_PASSWORD.*=\).*/ s//\1${NEO4J_PASSWORD}/" $NEO4J_ETL_CONFIG_FILE && \
    sed -i.bak "/\(EXT_VAR_GTAS_DB_HOST_NAME.*=\).*/ s//\1${DB_HOSTNAME}/" $NEO4J_ETL_CONFIG_FILEE && \
    sed -i.bak "/\(EXT_VAR_NEO4J_DB_HOST_NAME.*=\).*/ s//\1${NEO4J_HOSTNAME}/" $NEO4J_ETL_CONFIG_FILE && \
    sed -i.bak "/\(EXT_VAR_DELETE_OLD_DATA.*=\).*/ s//\1${ENABLE_DATA_RETENTION_POLICY}/" $NEO4J_ETL_CONFIG_FILE && \
  	sed -i.bak "/\(EXT_VAR_GTAS_DB_USER_NAME.*=\).*/ s//\1${GTAS_DB_REPORT_USER_NAME}/" $REPORT_ETL_CONFIG_FILE && \
    sed -i.bak "/\(EXT_VAR_GTAS_DB_PASSWORD.*=\).*/ s//\1${GTAS_DB_REPORT_USER_PASSWORD}/" $REPORT_ETL_CONFIG_FILE && \
    sed -i.bak "/\(EXT_VAR_ELSEARCH_USERNAME.*=\).*/ s//\1${ELASTICSEARCH_USER_NAME}/" $REPORT_ETL_CONFIG_FILE && \
    sed -i.bak "/\(EXT_VAR_ELSEARCH_PASSWORD.*=\).*/ s//\1${ELASTICSEARCH_USER_PASSWORD}/" $REPORT_ETL_CONFIG_FILE && \
    sed -i.bak "/\(EXT_VAR_KIBANA_USERNAME.*=\).*/ s//\1${KIBANA_USER_NAME}/" $REPORT_ETL_CONFIG_FILE && \
    sed -i.bak "/\(EXT_VAR_KIBANA_PASSWORD.*=\).*/ s//\1${KIBANA_USER_PASSWORD}/" $REPORT_ETL_CONFIG_FILE && \
    rm ${GTAS_ETL_HOME}/config/neo4j-etl/*.bak && \
    rm ${GTAS_ETL_HOME}/config/report-etl/*.bak && \
    java -jar /gtas-etl/gtas-etl-scheduler-1.jar



