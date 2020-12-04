FROM neo4j:3.5.11

ENV NEO4J_AUTH=neo4j/admin \
    NEO4J_dbms_active__database=gtas_db \
    NEO4J_dbms_security_auth__enabled=true \
    NEO4J_dbms_connectors_default__advertised__address=localhost \
    NEO4J_dbms_connector_http_enabled=false \
    NEO4J_dbms_connector_https_listen__address=:7473 \
    NEO4J_dbms_connector_https_enabled=true \
    NEO4J_dbms_connector_bolt_thread__pool__max__size=2500 \
    NEO4J_dbms_routing_driver_connection_connect__timeout=60s \
    NEO4j_dbms_ssl_policy_default__policy_trust_all=true

RUN mkdir -p /var/lib/neo4j/https/certificates/revoked /var/lib/neo4j/https/certificates/trusted

ENTRYPOINT cp -rf /var/lib/neo4j/https/certificates/key/neo4j.key /var/lib/neo4j/https/certificates/neo4j.key && \
    cp -rf /var/lib/neo4j/https/certificates/cert/neo4j.cert /var/lib/neo4j/https/certificates/neo4j.cert && \
    /docker-entrypoint.sh neo4j
