#!/bin/bash

parent_dir=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd "$parent_dir"

source ../set_env.sh

cd $ES_INSTALL_LOCATION

[[ -f $ES_INSTALL_LOCATION/elasticsearch/config/elasticsearch.keystore ]] || ($ES_INSTALL_LOCATION/elasticsearch/bin/elasticsearch-keystore create)

PW=$(openssl rand -base64 16;)
ELASTIC_PASSWORD="${ELASTIC_PASSWORD:-$PW}"
echo $ELASTIC_PASSWORD

echo "Setting password for elastic user"
(echo "$ELASTIC_PASSWORD" | $ES_INSTALL_LOCATION/elasticsearch/bin/elasticsearch-keystore add -x 'bootstrap.password')

# Create a folder to contain certificates in the configuration directory of your Elasticsearch node
mkdir -p $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl

yes | cp -f "$parent_dir"/../../../config/elasticsearch/instances.yml $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl

# Generate a certificate authority for your cluster.
$ES_INSTALL_LOCATION/elasticsearch/bin/elasticsearch-certutil ca --pem --silent --out $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/docker-cluster-ca.zip

echo "Unzip ca files..."
unzip $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/docker-cluster-ca.zip -d $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl

# Generate certificates and private keys
echo "Create cluster certs zipfile..."
$ES_INSTALL_LOCATION/elasticsearch/bin/elasticsearch-certutil cert --silent --pem --in $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/instances.yml --out $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/docker-cluster.zip --ca-cert $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/ca/ca.crt --ca-key $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/ca/ca.key

echo "Unzipping cluster certs zipfile..."
unzip $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/docker-cluster.zip -d $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/docker-cluster