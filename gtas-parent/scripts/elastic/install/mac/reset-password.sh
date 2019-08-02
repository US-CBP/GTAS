#!/bin/bash

parent_dir=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd "$parent_dir"

source ./set_env.sh

curl --cacert "${ES_INSTALL_LOCATION}/elasticsearch/config/certs/ssl/ca/ca.crt" --user elastic:Imnt/Jk4NOwS9HUILui22w== -X PUT "https://localhost:9200/_xpack/security/user/kibana/_password" -H 'Content-Type: application/json' -d'
{
  "password": "Pa$$word1"
}
'

