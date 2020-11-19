FROM docker.elastic.co/kibana/kibana:7.2.0

COPY ./install/docker/kibana/config/kibana.yml /usr/share/kibana/config/kibana.yml

USER root
RUN yum install -y wget
RUN wget https://github.com/jwilder/dockerize/releases/download/v0.6.1/dockerize-linux-amd64-v0.6.1.tar.gz
RUN tar -C /usr/local/bin -xvzf dockerize-linux-amd64-v0.6.1.tar.gz
COPY ./install/docker/kibana/wait-for-url .
RUN chmod 777 wait-for-url
RUN yum -y install dos2unix && dos2unix wait-for-url
USER kibana

COPY ./install/docker/kibana/kibana.default-dashboard.json .

CMD cp /kibana/kibana.keystore /usr/share/kibana/data/kibana.keystore && /usr/local/bin/kibana-docker