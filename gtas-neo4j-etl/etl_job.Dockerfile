FROM adoptopenjdk/maven-openjdk8 as scheduler-builder

COPY ./gtas-neo4j-scheduler /etl-project
WORKDIR /etl-project
RUN mvn clean install -Dskip.unit.tests=true



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
    GTAS_NEO4J_ETL_HOME=/gtas-neo4j-etl

RUN mkdir -p  ${GTAS_NEO4J_ETL_HOME}/config && \
    mkdir -p  ${GTAS_NEO4J_ETL_HOME}/job/temp && \
    mkdir -p  ${GTAS_NEO4J_ETL_HOME}/log


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

ENTRYPOINT export NEO4J_USER_NAME=$(cat /run/secrets/etl_neo4j_user) NEO4J_PASSWORD=$(cat /run/secrets/etl_neo4j_password) && \
    export GTAS_DB_USER_NAME=$(cat /run/secrets/mysql_etl_user) GTAS_DB_PASSWORD=$(cat /run/secrets/mysql_etl_password) && \
    sed -i.bak "/\(EXT_VAR_GTAS_DB_USER_NAME.*=\).*/ s//\1${GTAS_DB_USER_NAME}/" $CONFIG_FILE && \
    sed -i.bak "/\(EXT_VAR_GTAS_DB_PASSWORD.*=\).*/ s//\1${GTAS_DB_PASSWORD}/" $CONFIG_FILE && \
    sed -i.bak "/\(EXT_VAR_NEO4J_DB_USER_NAME.*=\).*/ s//\1${NEO4J_USER_NAME}/" $CONFIG_FILE && \
    sed -i.bak "/\(EXT_VAR_NEO4J_DB_PASSWORD.*=\).*/ s//\1${NEO4J_PASSWORD}/" $CONFIG_FILE && \
    sed -i.bak "/\(EXT_VAR_GTAS_DB_HOST_NAME.*=\).*/ s//\1${DB_HOSTNAME}/" $CONFIG_FILE && \
    sed -i.bak "/\(EXT_VAR_NEO4J_DB_HOST_NAME.*=\).*/ s//\1${NEO4J_HOSTNAME}/" $CONFIG_FILE && \
    rm ${GTAS_NEO4J_ETL_HOME}/config/*.bak && \
    java -jar /gtas-neo4j-etl/gtas-neo4j-job-scheduler-1.jar