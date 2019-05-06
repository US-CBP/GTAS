#!/bin/bash

#Import index patterns and set one of them as a the default index-pattern


#Default host
KIBANA_HOST='localhost'

#Default port
KIBANA_PORT=5601

URL="http://$KIBANA_HOST:$KIBANA_PORT"

# This will be the default index pattern
DEFAULT_INDEX_ID="flightpax" 

# echo "create index-pattern \"flightpax\""
# # Creates index pattern for flight pax
# curl -X POST -H "Content-Type: application/json" -H "kbn-xsrf: true" "$URL/api/saved_objects/index-pattern/flightpax" -d '{"attributes": {"title": "flightpax*"}}' 

# echo "\"flightpax\" created!!"

# echo "create index-pattern \"case\""
# # Creates index pattern for cases
# curl -X POST -H "Content-Type: application/json" -H "kbn-xsrf: true" "$URL/api/saved_objects/index-pattern/case" -d '{"attributes": {"title": "case*"}}' 
# echo "\"case\" created!!"

echo "making $DEFAULT_INDEX_ID the default index pattern"
# sets flightpax as the default index pattern 
curl -X POST -H "Content-Type: application/json" -H "kbn-xsrf: true" "$URL/api/kibana/settings/defaultIndex" -d '{"value": '\"$DEFAULT_INDEX_ID\"'}' 
echo "$DEFAULT_INDEX_ID is the default index pattern"