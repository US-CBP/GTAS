FROM docker.elastic.co/elasticsearch/elasticsearch:7.2.0

# COPY config/elasticsearch/update_template.sh /usr/local/bin/

# COPY config/logstash/cases_mapping.json /usr/share/elasticsearch/config/
# COPY config/logstash/flightpax_template.json /usr/share/elasticsearch/config/

ENV node.name=elasticsearch
ENV discovery.seed_hosts=elasticsearch
ENV cluster.initial_master_nodes=elasticsearch
ENV ES_JAVA_OPTS="-Xms512m -Xmx512m"
ENV xpack.security.http.ssl.enabled=false

EXPOSE 9200

# ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]

# CMD ["/usr/local/bin/update_template.sh"]
