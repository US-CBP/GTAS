FROM alpine as extractor
ENV PDI_VERSION=8.2.0.3-519 \
    PENTAHO_HOME=/opt/pentaho

RUN mkdir ${PENTAHO_HOME}
RUN /usr/bin/wget \
    # --progress=dot:giga \
    https://s3.amazonaws.com/kettle-neo4j/kettle-neo4j-remix-${PDI_VERSION}-REMIX.zip \
    -O /tmp/kettle-neo4j-remix-${PDI_VERSION}-REMIX.zip && \
    /usr/bin/unzip -q /tmp/kettle-neo4j-remix-${PDI_VERSION}-REMIX.zip -d  $PENTAHO_HOME 
    # rm /tmp/kettle-neo4j-remix-${PDI_VERSION}-REMIX.zip


FROM java:8-jre as javaJDK

ENV CONFIG_FILE="/gtas-neo4j-etl/config/gtas-neo4j-config.properties" \
    PENTAHO_HOME=/opt/pentaho/ \
    GTAS_DB_USER_NAME=root \
    NEO4J_USER_NAME=neo4j \
    GTAS_DB_PASSWORD=admin \
    NEO4J_PASSWORD=admin \
    DB_HOSTNAME=mariahost \
    NEO4J_HOSTNAME=neo4j \
    GTAS_NEO4J_ETL_HOME=/gtas-neo4j-etl
    
RUN mkdir ${PENTAHO_HOME}  
# Install pentaho
COPY --from=extractor /opt/pentaho/ ${PENTAHO_HOME}
COPY ./drivers/mariadb-java-client-2.2.1.jar ${PENTAHO_HOME}/data-integration/lib/

# copy .pnetaho to user's home directory 
 COPY ./pdi-conf/ /root


# etl job configs 
RUN mkdir -p  ${GTAS_NEO4J_ETL_HOME}
RUN mkdir -p  ${GTAS_NEO4J_ETL_HOME}/config
RUN mkdir -p  ${GTAS_NEO4J_ETL_HOME}/job
RUN mkdir -p  ${GTAS_NEO4J_ETL_HOME}/job/temp
RUN mkdir -p  ${GTAS_NEO4J_ETL_HOME}/log


COPY ./job ${GTAS_NEO4J_ETL_HOME}/job

COPY ./config ${GTAS_NEO4J_ETL_HOME}/config
COPY ./gtas-neo4j-job-scheduler-1.jar ${GTAS_NEO4J_ETL_HOME}/


# edit the gtas db (mariadb) user name and password to the config file
RUN sed -i.bak "/\(EXT_VAR_GTAS_DB_USER_NAME.*=\).*/ s//\1${GTAS_DB_USER_NAME}/" $CONFIG_FILE
RUN sed -i.bak "/\(EXT_VAR_GTAS_DB_PASSWORD.*=\).*/ s//\1${GTAS_DB_PASSWORD}/" $CONFIG_FILE

# Edit the neo4j user name and password
RUN sed -i.bak "/\(EXT_VAR_NEO4J_DB_USER_NAME.*=\).*/ s//\1${NEO4J_USER_NAME}/" $CONFIG_FILE
RUN sed -i.bak "/\(EXT_VAR_NEO4J_DB_PASSWORD.*=\).*/ s//\1${NEO4J_PASSWORD}/" $CONFIG_FILE

# change the gtas db and neo4j host names
RUN sed -i.bak "/\(EXT_VAR_GTAS_DB_HOST_NAME.*=\).*/ s//\1${DB_HOSTNAME}/" $CONFIG_FILE
RUN sed -i.bak "/\(EXT_VAR_NEO4J_DB_HOST_NAME.*=\).*/ s//\1${NEO4J_HOSTNAME}/" $CONFIG_FILE

RUN rm ${GTAS_NEO4J_ETL_HOME}/config/*.bak

WORKDIR ${GTAS_NEO4J_ETL_HOME}/
ENTRYPOINT ["java", "-jar", "/gtas-neo4j-etl/gtas-neo4j-job-scheduler-1.jar"]