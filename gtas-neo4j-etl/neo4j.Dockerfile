FROM neo4j:3.5.11

ENV NEO4J_AUTH=neo4j/admin \
    NEO4J_dbms_active__database=gtas_db \
    NEO4J_dbms_security_auth__enabled=true \
    NEO4J_dbms_connectors_default__advertised__address=localhost \
    NEO4J_dbms_connector_http_enabled=false \
    NEO4J_dbms_connector_https_listen__address=:7474 \
    NEO4J_dbms_connector_https_enabled=true \
    NEO4J_dbms_connector_bolt_advertised__address=localhost:443 \
    NEO4j_dbms_ssl_policy_default__policy_trust_all=true
