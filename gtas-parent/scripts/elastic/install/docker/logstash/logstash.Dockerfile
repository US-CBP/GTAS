FROM docker.elastic.co/logstash/logstash:7.2.0

USER root
RUN yum install -y wget
RUN wget https://github.com/jwilder/dockerize/releases/download/v0.6.1/dockerize-linux-amd64-v0.6.1.tar.gz
RUN tar -C /usr/local/bin -xvzf dockerize-linux-amd64-v0.6.1.tar.gz


ENV JAVA_OPTS="-Xms1g -Xmx1g"
ENV LOGSTASH_DIR=/usr/share/logstash
ENV LOGSTASH_LIB=/usr/share/logstash/logstash-core/lib/jars



RUN curl -o  /usr/share/logstash/logstash-core/lib/jars/mariadb-java-client-2.3.0.jar https://downloads.mariadb.com/Connectors/java/connector-java-2.3.0/mariadb-java-client-2.3.0.jar

RUN mkdir /temp-store && cp -R /usr/share/logstash/* /temp-store/
RUN sed -i '2icp -R -u -p /temp-store/* /usr/share/logstash/' /usr/local/bin/docker-entrypoint
RUN sed -i '3ils -R /' /usr/local/bin/docker-entrypoint

ENTRYPOINT ["dockerize", "-wait", "file:///usr/share/logstash/config/logstash.keystore", "-timeout", "1000s",  "/usr/local/bin/docker-entrypoint"]