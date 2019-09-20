#!/bin/bash

# Initialize kibana dashboard

CURRENT_DIR=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd $CURRENT_DIR

# Default host
KIBANA_HOST='kibana'
CONFIG_DIR=/config/kibana/config/kibana
CACERT=/usr/share/kibana/config/certs/kibana/kibana.crt

# Default port
KIBANA_PORT=5601

URL="https://$KIBANA_HOST:$KIBANA_PORT"

echo 'Kibana host: ' $KIBANA_HOST
echo 'Config Dir: ' $CONFIG_DIR
echo 'Certificate authority: ' $CACERT
echo 'Elastic Password: ' $ELASTICSEARCH_PASSWORD

# This will be the default index pattern
DEFAULT_INDEX_ID="96df0890-2ba8-11e9-a5e4-2bbcf61c6cb1" 

# Import dashbaord from json @kibana.default-dashboard.json 

# Note: The dashboard that is being imported needs to be exported using the kibana API 
# (./kibana.export-dashboard.sh will export and overrite kibana.default-dashboard.json). 
# The dashboard exported using the web UI will not work here since it has slightly different format

echo 'importing dashboard ....'

# Wait for Elasticsearch to start up.
until curl -f --cacert $cacert -u elastic:$ELASTICSEARCH_PASSWORD $URL -o /dev/null; do
    sleep 3
    echo "Waiting for Kibana..."
done

curl -k --cacert $CACERT  -u elastic:$ELASTICSEARCH_PASSWORD -X POST -H "Content-Type: application/json" -H "kbn-xsrf: true" "$URL/api/kibana/dashboards/import?force=true" -d @$CONFIG_DIR/kibana.default-dashboard.json

echo 'dashboard imported!!'

echo "making $DEFAULT_INDEX_ID the default index pattern"
# sets flightpax as the default index pattern 
curl -k --cacert $CACERT  -u elastic:$ELASTICSEARCH_PASSWORD -X POST -H "Content-Type: application/json" -H "kbn-xsrf: true" "$URL/api/kibana/settings/defaultIndex" -d '{"value": '\"$DEFAULT_INDEX_ID\"'}' 
echo "$DEFAULT_INDEX_ID is the default index pattern"