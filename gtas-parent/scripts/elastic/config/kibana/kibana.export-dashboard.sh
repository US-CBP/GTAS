#!/bin/bash

# Export kibana dashboard

CURRENT_DIR=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd $CURRENT_DIR

#Default host
KIBANA_HOST='localhost'

#Default port
KIBANA_PORT=5601

# export default gtas dashbaord
curl -X GET "http://$KIBANA_HOST:$KIBANA_PORT/api/kibana/dashboards/export?dashboard=7cfbbdc0-2e13-11e9-81a3-0f5bd8b0a7ac" -H 'kbn-xsrf: true' -o $CURRENT_DIR/kibana.default-dashboard.json

# export flight itinerary visualization

curl -X GET "http://$KIBANA_HOST:$KIBANA_PORT/api/saved_objects/visualization/29ac1380-66a9-11e9-9ffd-9d63a89be4bb" -H 'kbn-xsrf: true' -o $CURRENT_DIR/kibana.flight-itinerary-visualization.json 

