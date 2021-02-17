FROM adoptopenjdk/maven-openjdk8 as scheduler-builder

COPY ./gtas-neo4j-scheduler /etl-project
WORKDIR /etl-project
RUN mvn clean install -Dskip.unit.tests=false



FROM alpine as pentaho-extractor

ENV PDI_VERSION=8.2.0.3-519 

RUN mkdir /opt/pentaho
RUN /usr/bin/wget https://s3.amazonaws.com/kettle-neo4j/kettle-neo4j-remix-${PDI_VERSION}-REMIX.zip \
    -O /tmp/kettle-neo4j-remix-${PDI_VERSION}-REMIX.zip
RUN /usr/bin/unzip -q /tmp/kettle-neo4j-remix-${PDI_VERSION}-REMIX.zip -d  /opt/pentaho
RUN rm /tmp/kettle-neo4j-remix-${PDI_VERSION}-REMIX.zip



# FROM java:8-jre
FROM openjdk:8-jre-alpine

ENV CONFIG_FILE=/gtas-neo4j-etl/config/gtas-neo4j-config.properties \
    GTAS_NEO4J_ETL_HOME=/gtas-neo4j-etl \
    GTAS_REPORT_ETL_HOME=/gtas-report-etl \
    GTAS_REPORT_ETL_CONFIG_FILE=/gtas-report-etl/config/gtas-report-config.properties

RUN mkdir -p  ${GTAS_NEO4J_ETL_HOME}/config && \
    mkdir -p  ${GTAS_NEO4J_ETL_HOME}/job/temp && \
    mkdir -p  ${GTAS_NEO4J_ETL_HOME}/log

RUN mkdir -p  ${GTAS_REPORT_ETL_HOME}/config && \
	mkdir -p  ${GTAS_REPORT_ETL_HOME}/config/certs && \
    mkdir -p  ${GTAS_REPORT_ETL_HOME}/job/temp && \
 	mkdir -p  ${GTAS_REPORT_ETL_HOME}/job/index && \
	mkdir -p  ${GTAS_REPORT_ETL_HOME}/job/template && \
	mkdir -p  ${GTAS_REPORT_ETL_HOME}/job/workpad && \
    mkdir -p  ${GTAS_REPORT_ETL_HOME}/log


RUN apk add --no-cache bash 
COPY --from=scheduler-builder /root/.m2/repository/gov/gtas/gtas-neo4j-job-scheduler/1/gtas-neo4j-job-scheduler-1.jar /gtas-neo4j-etl

# Install pentaho
COPY --from=pentaho-extractor /opt/pentaho/ /opt/pentaho/
COPY ./gtas-neo4j-etl/drivers/mariadb-java-client-2.2.1.jar /opt/pentaho/data-integration/lib/

# copy .pnetaho to user's home directory 
COPY ./gtas-neo4j-etl/pdi-conf/ /root

# etl job configs 
COPY ./gtas-neo4j-etl/job ${GTAS_NEO4J_ETL_HOME}/job
COPY ./gtas-neo4j-etl/config ${GTAS_NEO4J_ETL_HOME}/config

WORKDIR ${GTAS_NEO4J_ETL_HOME}/

RUN apk add dos2unix
RUN dos2unix config/application.properties
RUN dos2unix config/gtas-neo4j-config.properties
RUN dos2unix config/run-record.properties


# report etl job and configs 
COPY ./gtas-report-etl/gtas-etl-scheduler.jar ${GTAS_REPORT_ETL_HOME}/
COPY ./gtas-report-etl/job ${GTAS_REPORT_ETL_HOME}/job
COPY ./gtas-report-etl/job/index ${GTAS_REPORT_ETL_HOME}/job/index
COPY ./gtas-report-etl/job/template ${GTAS_REPORT_ETL_HOME}/job/template
COPY ./gtas-report-etl/job/workpad ${GTAS_REPORT_ETL_HOME}/job/workpad
COPY ./gtas-report-etl/config ${GTAS_REPORT_ETL_HOME}/config

WORKDIR ${GTAS_REPORT_ETL_HOME}/

RUN apk add dos2unix
RUN dos2unix config/application.properties
RUN dos2unix config/gtas-neo4j-config.properties
RUN dos2unix config/run-record.properties
RUN dos2unix job/index/index.properties
RUN dos2unix job/template/template.properties
RUN dos2unix job/workpad/workpad.properties

ENTRYPOINT export NEO4J_USER_NAME=$(cat ${NEO4J_USER_PATH}) NEO4J_PASSWORD=$(cat ${NEO4J_PASSWORD_PATH}) && \
    export GTAS_DB_USER_NAME=$(cat ${MYSQL_USER_PATH}) GTAS_DB_PASSWORD=$(cat ${MYSQL_PASSWORD_PATH}) && \
    export GTAS_DB_REPORT_USER_NAME=$(cat ${ MYSQL_REPORT_USER_PATH}) GTAS_DB_REPORT_USER_PASSWORD=$(cat ${MYSQL_REPORT_USER_PASSWORD_PATH}) && \
    export ELASTICSEARCH_USER_NAME=$(cat ${ELASTICSEARCH_USER_PATH}) ELASTICSEARCH_USER_PASSWORD=$(cat ${ELASTICSEARCH_USER_PASSWORD_PATH}) && \
    export KIBANA_USER_NAME=$(cat ${KIBANA_USER_PATH}) KIBANA_USER_PASSWORD=$(cat ${KIBANA_USER_PASSWORD_PATH}) && \
    sed -i.bak "/\(EXT_VAR_GTAS_DB_USER_NAME.*=\).*/ s//\1${GTAS_DB_USER_NAME}/" $CONFIG_FILE && \
    sed -i.bak "/\(EXT_VAR_GTAS_DB_PASSWORD.*=\).*/ s//\1${GTAS_DB_PASSWORD}/" $CONFIG_FILE && \
    sed -i.bak "/\(EXT_VAR_NEO4J_DB_USER_NAME.*=\).*/ s//\1${NEO4J_USER_NAME}/" $CONFIG_FILE && \
    sed -i.bak "/\(EXT_VAR_NEO4J_DB_PASSWORD.*=\).*/ s//\1${NEO4J_PASSWORD}/" $CONFIG_FILE && \
    sed -i.bak "/\(EXT_VAR_GTAS_DB_HOST_NAME.*=\).*/ s//\1${DB_HOSTNAME}/" $CONFIG_FILE && \
    sed -i.bak "/\(EXT_VAR_NEO4J_DB_HOST_NAME.*=\).*/ s//\1${NEO4J_HOSTNAME}/" $CONFIG_FILE && \
    sed -i.bak "/\(EXT_VAR_DELETE_OLD_DATA.*=\).*/ s//\1${ENABLE_DATA_RETENTION_POLICY}/" $CONFIG_FILE && \
  	sed -i.bak "/\(EXT_VAR_GTAS_DB_USER_NAME.*=\).*/ s//\1${GTAS_DB_REPORT_USER_NAME}/" $GTAS_REPORT_ETL_CONFIG_FILE && \
    sed -i.bak "/\(EXT_VAR_GTAS_DB_PASSWORD.*=\).*/ s//\1${GTAS_DB_REPORT_USER_PASSWORD}/" $GTAS_REPORT_ETL_CONFIG_FILE && \
    sed -i.bak "/\(EXT_VAR_ELSEARCH_USERNAME.*=\).*/ s//\1${ELASTICSEARCH_USER_NAME}/" $GTAS_REPORT_ETL_CONFIG_FILE && \
    sed -i.bak "/\(EXT_VAR_ELSEARCH_PASSWORD.*=\).*/ s//\1${ELASTICSEARCH_USER_PASSWORD}/" $GTAS_REPORT_ETL_CONFIG_FILE && \
    sed -i.bak "/\(EXT_VAR_KIBANA_USERNAME.*=\).*/ s//\1${KIBANA_USER_NAME}/" $GTAS_REPORT_ETL_CONFIG_FILE && \
    sed -i.bak "/\(EXT_VAR_KIBANA_PASSWORD.*=\).*/ s//\1${KIBANA_USER_PASSWORD}/" $GTAS_REPORT_ETL_CONFIG_FILE && \
    rm ${GTAS_NEO4J_ETL_HOME}/config/*.bak && \
    java -jar /gtas-neo4j-etl/gtas-neo4j-job-scheduler-1.jar



