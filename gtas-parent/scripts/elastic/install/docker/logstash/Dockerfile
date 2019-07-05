FROM docker.elastic.co/logstash/logstash:6.5.4

COPY config/logstash/pipelines.yml /usr/share/logstash/config/pipelines.yml

COPY config/logstash/logstash-cases.conf /usr/share/logstash/config/logstash-cases.conf
COPY config/logstash/logstash-flightpax.conf /usr/share/logstash/config/logstash-flightpax.conf
COPY config/logstash/logstash-flight_legs.conf /usr/share/logstash/config/logstash-flight_legs.conf

COPY config/logstash/flightpax_script.sql /usr/share/logstash/config/flightpax_script.sql
COPY config/logstash/cases_script.sql /usr/share/logstash/config/cases_script.sql
COPY config/logstash/flight_legs.sql /usr/share/logstash/config/flight_legs.sql

COPY config/logstash/cases_mapping.json /usr/share/logstash/config/cases_mapping.json
COPY config/logstash/flightpax_template.json /usr/share/logstash/config/flightpax_template.json

ENV ELASTIC_HOST=elasticsearch
ENV DATABASE_HOST=mariahost

RUN curl -o  /usr/share/logstash/config/mariadb-java-client-2.3.0.jar https://downloads.mariadb.com/Connectors/java/connector-java-2.3.0/mariadb-java-client-2.3.0.jar

# CMD ["/opt/config/elasticsearch/update_template.sh"]