FROM docker.elastic.co/elasticsearch/elasticsearch:6.5.4

COPY config/elasticsearch/update_template.sh /usr/local/bin/

COPY config/logstash/cases_mapping.json /usr/share/elasticsearch/config/
COPY config/logstash/flightpax_template.json /usr/share/elasticsearch/config/

EXPOSE 9200