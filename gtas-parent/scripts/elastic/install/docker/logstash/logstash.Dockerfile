FROM docker.elastic.co/logstash/logstash:7.2.0

USER root
RUN yum install -y wget
RUN wget https://github.com/jwilder/dockerize/releases/download/v0.6.1/dockerize-linux-amd64-v0.6.1.tar.gz
RUN tar -C /usr/local/bin -xvzf dockerize-linux-amd64-v0.6.1.tar.gz
RUN sed -i '2icp /keystore/logstash.keystore /usr/share/logstash/config/logstash.keystore' /usr/local/bin/docker-entrypoint
RUN sed -i '4ichown logstash:logstash /usr/share/logstash/config/logstash.keystore' /usr/local/bin/docker-entrypoint
RUN cat /usr/local/bin/docker-entrypoint

RUN yum install -y dos2unix


USER logstash


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

USER root
RUN dos2unix /usr/share/logstash/config/flightpax_template.json
RUN dos2unix /usr/share/logstash/config/cases_mapping.json
RUN dos2unix /usr/share/logstash/config/flight_legs.sql
RUN dos2unix /usr/share/logstash/config/cases_script.sql
RUN dos2unix /usr/share/logstash/config/flightpax_script.sql
RUN dos2unix /usr/share/logstash/config/logstash-flight_legs.conf
RUN dos2unix /usr/share/logstash/config/logstash-flightpax.conf
RUN dos2unix /usr/share/logstash/config/logstash-cases.conf

RUN chown logstash:logstash /usr/share/logstash/config/flightpax_template.json
RUN chown logstash:logstash /usr/share/logstash/config/cases_mapping.json
RUN chown logstash:logstash /usr/share/logstash/config/flight_legs.sql
RUN chown logstash:logstash /usr/share/logstash/config/cases_script.sql
RUN chown logstash:logstash /usr/share/logstash/config/flightpax_script.sql
RUN chown logstash:logstash /usr/share/logstash/config/logstash-flight_legs.conf
RUN chown logstash:logstash /usr/share/logstash/config/logstash-flightpax.conf
RUN chown logstash:logstash /usr/share/logstash/config/logstash-cases.conf
RUN chown logstash:logstash /usr/share/logstash/config/pipelines.yml

USER logstash


ENV JAVA_OPTS="-Xms1g -Xmx2g"
ENV LOGSTASH_DIR=/usr/share/logstash
ENV LOGSTASH_LIB=/usr/share/logstash/logstash-core/lib/jars

RUN curl -o  /usr/share/logstash/logstash-core/lib/jars/mariadb-java-client-2.3.0.jar https://downloads.mariadb.com/Connectors/java/connector-java-2.3.0/mariadb-java-client-2.3.0.jar

CMD ["/usr/local/bin/docker-entrypoint"]