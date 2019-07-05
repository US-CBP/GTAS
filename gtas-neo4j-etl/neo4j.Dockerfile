FROM neo4j

ENV NEO4J_AUTH=neo4j/admin \
    NEO4J_dbms_active__database=gtas_db \
    NEO4J_dbms_security_auth__enabled=true \
    NEO4J_dbms_connectors_default__advertised__address=localhost \
    NEO4J_dbms_connector_bolt_listen__address=:7687 \
    NEO4J_dbms_connector_http_listen__address=:7474 
    

