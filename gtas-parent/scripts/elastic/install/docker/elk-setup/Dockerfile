FROM docker.elastic.co/kibana/kibana:7.2.0 as kibana-base
FROM docker.elastic.co/elasticsearch/elasticsearch:7.2.0 as elasticsearch-base
FROM docker.elastic.co/logstash/logstash:7.2.0 as logstash-base

USER root
RUN mkdir elasticsearch && mkdir kibana
COPY --from=kibana-base /usr/share/kibana/ kibana
COPY --from=elasticsearch-base /usr/share/elasticsearch/ elasticsearch

VOLUME /kibana-keystore/
VOLUME /elasticsearch-keystore/
VOLUME /logstash-keystore/

COPY ./install/docker/elk-setup/kibana.default-dashboard.json .

RUN ./elasticsearch/bin/elasticsearch-keystore create


ENTRYPOINT cat /run/secrets/elasticsearch_kibana_user && echo $(cat /run/secrets/elasticsearch_kibana_user) | ./elasticsearch/bin/elasticsearch-keystore add kibana_user \
	&& echo $(cat /run/secrets/elasticsearch_kibana_password) | ./elasticsearch/bin/elasticsearch-keystore add kibana_password \
	&& cp ./elasticsearch/config/elasticsearch.keystore /elasticsearch-keystore/ \
	&& export LOGSTASH_KEYSTORE_PASS=$(cat /run/secrets/logstash_keystore_password) \
	&& ./bin/logstash-keystore create \
	&& echo $(cat /run/secrets/mysql_logstash_user) | ./bin/logstash-keystore add MARIADB_USER \
	&& echo $(cat /run/secrets/mysql_logstash_password) | ./bin/logstash-keystore add MARIADB_PASSWORD \
	&& echo $(cat /run/secrets/elastic_user) | ./bin/logstash-keystore add ELASTIC_USER \
	&& echo $(cat /run/secrets/elastic_password) | ./bin/logstash-keystore add ELASTIC_PASSWORD \
	&& cp ./config/logstash.keystore /logstash-keystore/ \
	&& until curl -s http://${KIBANA_HOST}:5601/login -o /dev/null; do sleep 10 && echo "Waiting for kibana..."; done \
	&& curl -X POST -H "Content-Type: application/json" -H "kbn-xsrf: true" "https://${KIBANA_HOST}:5601/api/kibana/dashboards/import?force=true" -d @kibana.default-dashboard.json \
	&& curl -X POST -H "Content-Type: application/json" -H "kbn-xsrf: true" "https://${KIBANA_HOST}:5601/api/kibana/settings/defaultIndex" -d '{"value": "96df0890-2ba8-11e9-a5e4-2bbcf61c6cb1"}' 