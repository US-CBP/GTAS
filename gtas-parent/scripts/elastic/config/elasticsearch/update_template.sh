#!/bin/bash

CURRENT_DIR=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd $CURRENT_DIR

set -e

ES_HOST="http://localhost:9200"

until $(curl --output /dev/null --silent --head --fail "$ES_HOST"); do
    printf '.'
    sleep 10
done

# First wait for ES to start...
response=$(curl $ES_HOST)

until [ "$response" = "200" ]; do
    response=$(curl --write-out %{http_code} --silent --output /dev/null "$ES_HOST")
    >&2 echo "Elastic Search is unavailable - trying again in 5 seconds ..."
    sleep 5
done


curl -X DELETE "localhost:9200/_template/case*"

curl -X DELETE "localhost:9200/case"

curl -XPUT -H 'Content-Type: application/json' http://localhost:9200/_template/case -d@../logstash/cases_mapping.json

curl -X DELETE "localhost:9200/_template/flightpax_template"

curl -X DELETE "localhost:9200/flightpax"

curl -XPUT -H 'Content-Type: application/json' http://localhost:9200/_template/flightpax_template -d@../logstash/flightpax_template.json