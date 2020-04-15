FROM wcogtas/elasticsearch:ppc64le

RUN rm -f /usr/share/elasticsearch/config/elasticsearch.yml
COPY ./install/docker/elasticsearch/config/elasticsearch.yml /usr/share/elasticsearch/config

ENV node.name=elasticsearch
ENV discovery.seed_hosts=elasticsearch
ENV cluster.initial_master_nodes=elasticsearch
ENV ES_JAVA_OPTS="-Xms512m -Xmx512m"


ENV xpack.security.enabled=true
ENV xpack.security.http.ssl.enabled=true
ENV xpack.security.transport.ssl.enabled=true

ENV xpack.security.http.ssl.key=/usr/share/elasticsearch/config/certs/elasticsearch/elasticsearch-node1.key
ENV xpack.security.http.ssl.certificate=/usr/share/elasticsearch/config/certs/elasticsearch/elasticsearch-node1.crt
ENV xpack.security.http.ssl.certificate_authorities=/usr/share/elasticsearch/config/certs/elasticsearch/elastic-ca.crt

ENV xpack.security.transport.ssl.key=/usr/share/elasticsearch/config/certs/elasticsearch/elasticsearch-node1.key
ENV xpack.security.transport.ssl.certificate=/usr/share/elasticsearch/config/certs/elasticsearch/elasticsearch-node1.crt
ENV xpack.security.transport.ssl.certificate_authorities=/usr/share/elasticsearch/config/certs/elasticsearch/elastic-ca.crt

ENV xpack.graph.enabled=false
ENV xpack.ml.enabled=false
ENV xpack.monitoring.enabled=false
ENV xpack.watcher.enabled=false

RUN mkdir -p /usr/share/elasticsearch/data
RUN chown -R 1000:1000 /usr/share/elasticsearch

RUN sed -i '2iwhile [ ! -f /usr/share/elasticsearch/config/elasticsearch.keystore ]; do sleep 5; done' /usr/local/bin/docker-entrypoint.sh

USER 1000
CMD [ "/usr/local/bin/docker-entrypoint.sh" ]