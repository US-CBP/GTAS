FROM neo4j:3.5.11

ENV NEO4J_dbms_active__database=gtas_db \
    NEO4J_dbms_connector_http_enabled=true \
    NEO4J_dbms_connector_https_listen__address=:7473 \
    NEO4J_dbms_connector_https_enabled=true

RUN mkdir -p /var/lib/neo4j/https/certificates/revoked /var/lib/neo4j/https/certificates/trusted

ENTRYPOINT cp -f /var/lib/neo4j/https/certificates/key/neo4j.key /var/lib/neo4j/https/certificates/neo4j.key && \
    cp -f /var/lib/neo4j/https/certificates/cert/neo4j.cert /var/lib/neo4j/https/certificates/neo4j.cert && \
    /docker-entrypoint.sh neo4j
