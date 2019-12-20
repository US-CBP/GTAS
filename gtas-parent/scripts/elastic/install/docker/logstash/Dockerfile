FROM docker.elastic.co/logstash/logstash:7.2.0

USER root
RUN mkdir /config && chown -R logstash /config && chmod 0755 -R /config
RUN mkdir /usr/share/logstash/pipeline/config/
RUN chmod 0755 -R /usr/share/logstash/pipeline/config/ && chown -R logstash /usr/share/logstash/pipeline/config/

USER 1000
COPY config/logstash/pipelines.yml /usr/share/logstash/config/pipelines.yml
RUN rm -f /usr/share/logstash/pipeline/logstash.conf

COPY --chown=logstash config/logstash/logstash-cases.conf /usr/share/logstash/config/logstash-cases.conf
COPY --chown=logstash config/logstash/logstash-flightpax.conf /usr/share/logstash/config/logstash-flightpax.conf
COPY --chown=logstash config/logstash/logstash-flight_legs.conf /usr/share/logstash/config/logstash-flight_legs.conf

COPY --chown=logstash config/logstash/flightpax_script.sql /usr/share/logstash/config/flightpax_script.sql
COPY --chown=logstash config/logstash/cases_script.sql /usr/share/logstash/config/cases_script.sql
COPY --chown=logstash config/logstash/flight_legs.sql /usr/share/logstash/config/flight_legs.sql

COPY --chown=logstash config/logstash/cases_mapping.json /usr/share/logstash/config/cases_mapping.json
COPY --chown=logstash config/logstash/flightpax_template.json /usr/share/logstash/config/flightpax_template.json


ENV JAVA_OPTS="-Xms1g -Xmx1g"
ENV LOGSTASH_DIR=/usr/share/logstash
ENV LOGSTASH_LIB=/usr/share/logstash/logstash-core/lib/jars



RUN curl -o  /usr/share/logstash/logstash-core/lib/jars/mariadb-java-client-2.3.0.jar https://downloads.mariadb.com/Connectors/java/connector-java-2.3.0/mariadb-java-client-2.3.0.jar