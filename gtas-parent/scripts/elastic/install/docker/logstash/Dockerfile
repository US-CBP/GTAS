FROM docker.elastic.co/logstash/logstash:7.2.0


ENV JAVA_OPTS="-Xms1g -Xmx1g"
ENV LOGSTASH_DIR=/usr/share/logstash
ENV LOGSTASH_LIB=/usr/share/logstash/logstash-core/lib/jars

USER root

ENV XPACK_MONITORING_ELASTICSEARCH_SSL_CERTIFICATEAUTHORITY=/run/secrets/elasticsearch_ca



RUN curl -o  /usr/share/logstash/logstash-core/lib/jars/mariadb-java-client-2.3.0.jar https://downloads.mariadb.com/Connectors/java/connector-java-2.3.0/mariadb-java-client-2.3.0.jar