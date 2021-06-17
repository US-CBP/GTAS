FROM docker.elastic.co/elasticsearch/elasticsearch:7.12.1

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

ENV xpack.security.http.ssl.key=/usr/share/elasticsearch/config/elasticsearch-node1.key
ENV xpack.security.http.ssl.certificate=/usr/share/elasticsearch/config/elasticsearch-node1.crt
ENV xpack.security.http.ssl.certificate_authorities=/usr/share/elasticsearch/config/elastic-ca.crt

ENV xpack.security.transport.ssl.key=/usr/share/elasticsearch/config/elasticsearch-node1.key
ENV xpack.security.transport.ssl.certificate=/usr/share/elasticsearch/config/elasticsearch-node1.crt
ENV xpack.security.transport.ssl.certificate_authorities=/usr/share/elasticsearch/config/elastic-ca.crt
ENV xpack.http.ssl.verification_mode=certificate

ENV xpack.graph.enabled=false
ENV xpack.ml.enabled=false
ENV xpack.monitoring.enabled=false
ENV xpack.watcher.enabled=false

RUN sed -i '2icp /keystore/elasticsearch.keystore /usr/share/elasticsearch/config/elasticsearch.keystore' /usr/local/bin/docker-entrypoint.sh
RUN sed -i '3icp /usr/share/elasticsearch/key/elasticsearch-node1.key /usr/share/elasticsearch/config/elasticsearch-node1.key' /usr/local/bin/docker-entrypoint.sh
RUN sed -i '4icp /usr/share/elasticsearch/crt/elasticsearch-node1.crt /usr/share/elasticsearch/config/elasticsearch-node1.crt' /usr/local/bin/docker-entrypoint.sh
RUN sed -i '5icp /usr/share/elasticsearch/ca/elastic-ca.crt /usr/share/elasticsearch/config/elastic-ca.crt' /usr/local/bin/docker-entrypoint.sh


CMD chown -R 1000:1000 /usr/share/elasticsearch && /usr/local/bin/docker-entrypoint.sh