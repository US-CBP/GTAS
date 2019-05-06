#!/bin/bash

CURRENT_DIR=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd $CURRENT_DIR

curl -X DELETE "localhost:9200/_template/case*"

curl -X DELETE "localhost:9200/case"

curl -XPUT -H 'Content-Type: application/json' http://localhost:9200/_template/case -d@../logstash/cases_mapping.json

curl -X DELETE "localhost:9200/_template/flightpax_template"

curl -X DELETE "localhost:9200/flightpax"

curl -XPUT -H 'Content-Type: application/json' http://localhost:9200/_template/flightpax_template -d@../logstash/flightpax_template.json