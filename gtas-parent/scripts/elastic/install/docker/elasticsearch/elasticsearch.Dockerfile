FROM docker.elastic.co/elasticsearch/elasticsearch:7.2.0

RUN yum install -y wget
RUN wget https://github.com/jwilder/dockerize/releases/download/v0.6.1/dockerize-linux-amd64-v0.6.1.tar.gz
RUN tar -C /usr/local/bin -xvzf dockerize-linux-amd64-v0.6.1.tar.gz

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

# USER elasticsearch

ENTRYPOINT ["dockerize", "-wait", "file:///usr/share/elasticsearch/config/elasticsearch.keystore", "-timeout", "1000s", "/usr/local/bin/docker-entrypoint.sh"]