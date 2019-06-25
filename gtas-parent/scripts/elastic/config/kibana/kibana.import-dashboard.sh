#!/bin/bash

# Initialize kibana dashboard

CURRENT_DIR=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd $CURRENT_DIR

#Default host
KIBANA_HOST='localhost'

#Default port
KIBANA_PORT=5601

URL="http://$KIBANA_HOST:$KIBANA_PORT"

# This will be the default index pattern
DEFAULT_INDEX_ID="96df0890-2ba8-11e9-a5e4-2bbcf61c6cb1" 

# Import dashbaord from json @kibana.default-dashboard.json 

# Note: The dashboard that is being imported needs to be exported using the kibana API 
# (./kibana.export-dashboard.sh will export and overrite kibana.default-dashboard.json). 
# The dashboard exported using the web UI will not work here since it has slightly different format

echo 'importing  ....'

curl -X POST -H "Content-Type: application/json" -H "kbn-xsrf: true" "$URL/api/kibana/dashboards/import?force=true" -d @kibana.default-dashboard.json

curl -X POST -H "Content-Type: application/json" -H "kbn-xsrf: true" "$URL/api/saved_objects/visualization/29ac1380-66a9-11e9-9ffd-9d63a89be4bb?force=true" -d $CURRENT_DIR/kibana.flight-itinerary-visualization.json

echo ' imported!!'

echo "making $DEFAULT_INDEX_ID the default index pattern"
# sets flightpax as the default index pattern 
curl -X POST -H "Content-Type: application/json" -H "kbn-xsrf: true" "$URL/api/kibana/settings/defaultIndex" -d '{"value": '\"$DEFAULT_INDEX_ID\"'}' 
echo "$DEFAULT_INDEX_ID is the default index pattern"