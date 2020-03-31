FROM wcogtas/kibana:ppc64le as kibana-base
FROM wcogtas/elasticsearch:ppc64le as elasticsearch-base
FROM wcogtas/logstash:ppc64le as logstash-base

USER root
WORKDIR /usr/share

RUN mkdir kibana elasticsearch /kibana-conf
COPY --from=kibana-base /usr/share/kibana/ kibana
COPY --from=elasticsearch-base /usr/share/elasticsearch/ elasticsearch

RUN mkdir /usr/share/logstash/pipeline/config/
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

RUN mkdir /elasticsearch-conf /logstash-conf
RUN cp -r ./elasticsearch/config/* /elasticsearch-conf/
RUN cp -r /usr/share/logstash/config/* /logstash-conf/

RUN yum update -y && yum install -y java-1.8.0-openjdk


RUN curl -sL https://raw.githubusercontent.com/creationix/nvm/v0.34.0/install.sh -o install_nvm.sh
RUN /bin/bash install_nvm.sh && .  /root/.nvm/nvm.sh && nvm install 10.15.2 && nvm use 10.15.2 && npm install -g yarn

ENV NVM_DIR /root/.nvm
ENV NODE_PATH $NVM_DIR/v10.15.2/lib/node_modules
ENV PATH $NVM_DIR/versions/node/v10.15.2/bin:$PATH

RUN sed -i '17d' /usr/share/kibana/bin/kibana-keystore
RUN sed -i '17iNODE=/root/.nvm/versions/node/v10.15.2/bin/node' /usr/share/kibana/bin/kibana-keystore

# RUN chown -R 1000:1000 /logstash-conf /elasticsearch-conf ./elasticsearch ./kibana

COPY ./install/docker/elk-setup/kibana.default-dashboard.json .
CMD echo y | ./elasticsearch/bin/elasticsearch-keystore create \
	&& ./elasticsearch/bin/elasticsearch-keystore add bootstrap.password </run/secrets/elastic_bootstrap_password \
	&& cp ./elasticsearch/config/elasticsearch.keystore /elasticsearch-conf/elasticsearch.keystore \
	&& export LOGSTASH_KEYSTORE_PASS=$(cat /run/secrets/logstash_keystore_password) \
	&& echo y | ./logstash/bin/logstash-keystore create \
	&& ./logstash/bin/logstash-keystore add MARIADB_USER </run/secrets/mysql_logstash_user \
	&& ./logstash/bin/logstash-keystore add MARIADB_PASSWORD </run/secrets/mysql_logstash_password \
	&& ./logstash/bin/logstash-keystore add ES_PASSWORD </run/secrets/elastic_password \
	&& cp ./logstash/config/logstash.keystore /logstash-conf/logstash.keystore \
	&& echo y | ./kibana/bin/kibana-keystore --allow-root create \
	&& echo kibana | ./kibana/bin/kibana-keystore --allow-root add elasticsearch.username --stdin \
	&& ./kibana/bin/kibana-keystore --allow-root add elasticsearch.password --stdin </run/secrets/elasticsearch_kibana_password \
	&& cp -r ./kibana/data/* /kibana-conf/ && chown -R 1000:1000 /kibana-conf \
	&& until [ $(curl -k -s -o /dev/null -w "%{http_code}"  https://${ELASTICSEARCH_HOST}:9200/) == 401 ]; do sleep 10 && echo "Waiting for elasticsearch..."; done \
	&& curl -k -s -H 'Content-Type:application/json' -XPUT "https://elastic:$(cat /run/secrets/elastic_bootstrap_password)@${ELASTICSEARCH_HOST}:9200/_security/user/kibana/_password" -d "{\"password\": \"$(cat /run/secrets/elasticsearch_kibana_password)\"}" \
	&& curl -k -s -H 'Content-Type:application/json' -XPUT "https://elastic:$(cat /run/secrets/elastic_bootstrap_password)@${ELASTICSEARCH_HOST}:9200/_security/user/logstash_system/_password" -d "{\"password\": \"$(cat /run/secrets/elastic_password)\"}" \
	&& curl -k -s -H 'Content-Type:application/json' -XPUT "https://elastic:$(cat /run/secrets/elastic_bootstrap_password)@${ELASTICSEARCH_HOST}:9200/case" -d '{ "settings" : { "index" : { } }}' \
	&& until [ $(curl -k -s -o /dev/null -w "%{http_code}" -u kibana:$(cat /run/secrets/elasticsearch_kibana_password)  https://${KIBANA_HOST}:5601/login) == 200 ]; do sleep 10 && echo "Waiting for kibana..."; done \
	&& curl -k -u elastic:$(cat /run/secrets/elastic_bootstrap_password) -X POST -H "Content-Type: application/json" -H "kbn-xsrf: true" "https://${KIBANA_HOST}:5601/api/telemetry/v2/optIn" -d '{"enabled":false}' \
	&& curl -k -u elastic:$(cat /run/secrets/elastic_bootstrap_password) -X POST -H "Content-Type: application/json" -H "kbn-xsrf: true" "https://${KIBANA_HOST}:5601/api/kibana/dashboards/import?force=true" -d @kibana.default-dashboard.json \
	&& curl -k -u elastic:$(cat /run/secrets/elastic_bootstrap_password) -X POST -H "Content-Type: application/json" -H "kbn-xsrf: true" "https://${KIBANA_HOST}:5601/api/kibana/settings/defaultIndex" -d '{"value": "96df0890-2ba8-11e9-a5e4-2bbcf61c6cb1"}' 