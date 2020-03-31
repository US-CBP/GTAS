FROM wcogtas/kibana:ppc64le


COPY ./install/docker/kibana/config/kibana.yml /usr/share/kibana/config/kibana.yml

# USER root
# RUN yum install -y wget
# RUN wget https://github.com/jwilder/dockerize/releases/download/v0.6.1/dockerize-linux-amd64-v0.6.1.tar.gz
# RUN tar -C /usr/local/bin -xvzf dockerize-linux-amd64-v0.6.1.tar.gz
USER kibana

ENTRYPOINT while [ ! -f /usr/share/kibana/data/kibana.keystore ]; do sleep 5; done && /usr/share/kibana/bin/kibana