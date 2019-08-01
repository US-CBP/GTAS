FROM neo4j

ENV NEO4J_AUTH=neo4j/admin \
    NEO4J_dbms_active__database=gtas_db \
    NEO4J_dbms_security_auth__enabled=true \
    NEO4J_dbms_connectors_default__advertised__address=localhost \
    NEO4J_dbms_connector_bolt_listen__address=:7687 \
    NEO4J_dbms_connector_http_listen__address=:7474 \
    CONFIG_FILE="/gtas-neo4j-etl/config/gtas-neo4j-config.properties" \
    GTAS_DB_USER_NAME=root \
    NEO4J_USER_NAME=neo4j \
    GTAS_DB_PASSWORD=admin \
    NEO4J_PASSWORD=admin 
    #TODO pentaho envs

RUN mkdir -p  /gtas-neo4j-etl
RUN mkdir -p  /gtas-neo4j-etl/config
RUN mkdir -p  /gtas-neo4j-etl/job
RUN mkdir -p  /gtas-neo4j-etl/job/temp
RUN mkdir -p  /gtas-neo4j-etl/log


COPY ./job /gtas-neo4j-etl/job
COPY ./job /gtas-neo4j-etl/job

COPY ./config /gtas-neo4j-etl/config

# edit the gtas db (mariadb) user name and password to the config file
RUN sed -i.bak "/\(EXT_VAR_GTAS_DB_USER_NAME.*=\).*/ s//\1${GTAS_DB_USER_NAME}/" $CONFIG_FILE
RUN sed -i.bak "/\(EXT_VAR_GTAS_DB_PASSWORD.*=\).*/ s//\1${GTAS_DB_PASSWORD}/" $CONFIG_FILE

# Edit the neo4j user name and password
RUN sed -i.bak "/\(EXT_VAR_NEO4J_DB_USER_NAME.*=\).*/ s//\1${NEO4J_USER_NAME}/" $CONFIG_FILE
RUN sed -i.bak "/\(EXT_VAR_NEO4J_DB_PASSWORD.*=\).*/ s//\1${NEO4J_PASSWORD}/" $CONFIG_FILE

RUN rm /gtas-neo4j-etl/config/*.bak

# Install OpenJDK-8(required for pentaho)
#TODO

# Install pentaho
#TODO