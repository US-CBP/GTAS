FROM docker.elastic.co/kibana/kibana:7.2.0

COPY ./install/docker/kibana/config/kibana.yml /usr/share/kibana/config/kibana.yml

USER root
RUN yum install -y wget
RUN wget https://github.com/jwilder/dockerize/releases/download/v0.6.1/dockerize-linux-amd64-v0.6.1.tar.gz
RUN tar -C /usr/local/bin -xvzf dockerize-linux-amd64-v0.6.1.tar.gz
USER kibana

ENTRYPOINT ["dockerize", "-wait", "file:///usr/share/kibana/data/kibana.keystore", "-timeout", "1000s", "/usr/local/bin/kibana-docker"]