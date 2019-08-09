#!/bin/bash

parent_dir=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd "$parent_dir"

source ./set_env.sh

cd $ES_INSTALL_LOCATION

# remove existing keystore
if [[ -f $ES_INSTALL_LOCATION/elasticsearch/config/elasticsearch.keystore ]]
then
    rm -rf $ES_INSTALL_LOCATION/elasticsearch/config/elasticsearch.keystore
fi

# create new keystory
($ES_INSTALL_LOCATION/elasticsearch/bin/elasticsearch-keystore create)

PW=$(openssl rand -base64 16;)
ELASTIC_PASSWORD="${ELASTIC_PASSWORD:-$PW}"
echo $ELASTIC_PASSWORD

echo "Setting password for elastic user"
(echo "$ELASTIC_PASSWORD" | $ES_INSTALL_LOCATION/elasticsearch/bin/elasticsearch-keystore add -x 'bootstrap.password')

# Kibana settings
if [[ -f $ES_INSTALL_LOCATION/kibana/data/kibana.keystore ]] 
then
    echo 'Deleting existing kibana keystore ... '
    rm -rf $ES_INSTALL_LOCATION/kibana/data/kibana.keystore 
fi

echo 'Create new kibana keystore ... '
($ES_INSTALL_LOCATION/kibana/bin/kibana-keystore create)

# echo "Setting password for kibana user"
(echo "$ELASTIC_PASSWORD" | $ES_INSTALL_LOCATION/kibana/bin/kibana-keystore add elasticsearch.password --stdin)
###

# Create keystore if it does not exist
if [[ -f $ES_INSTALL_LOCATION/logstash/config/logstash.keystore ]] 
then 
    rm -rf $ES_INSTALL_LOCATION/logstash/config/logstash.keystore
fi
($ES_INSTALL_LOCATION/logstash/bin/logstash-keystore --path.settings $ES_INSTALL_LOCATION/logstash/config create)

# echo "Setting password for logstash_user user"
(echo "$ELASTIC_PASSWORD" | $ES_INSTALL_LOCATION/logstash/bin/logstash-keystore add ES_PWD)

# # remove old certificate files
# if [[ -d $ES_INSTALL_LOCATION/elasticsearch/config/certs ]]
# then 
#     rm -rf $ES_INSTALL_LOCATION/elasticsearch/config/certs
    
# Create a folder to contain certificates in the configuration directory of your Elasticsearch node
mkdir -p $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl

yes | cp -f "$parent_dir"/../../config/elasticsearch/instances.yml $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl

# check if old docker-cluster-ca.zip exists, if it does remove and create a new one.
if [ -f $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/docker-cluster-ca.zip ]; then
    echo "Remove old ca zip..."
    rm $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/docker-cluster-ca.zip
fi

# Generate a certificate authority for your cluster.
$ES_INSTALL_LOCATION/elasticsearch/bin/elasticsearch-certutil ca --pem --silent --out $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/docker-cluster-ca.zip

# check if ca directory exists, if does, remove then unzip new files
if [ -d $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/ca ]; then
    echo "CA directory exists, removing..."
    rm -rf $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/ca
fi

echo "Unzip ca files..."
unzip $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/docker-cluster-ca.zip -d $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl

# check if certs zip exist. If it does remove and create a new one.
if [ -f $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/docker-cluster.zip ]; then
    echo "Remove old docker-cluster.zip zip..."
    rm $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/docker-cluster.zip
fi
# Generate certificates and private keys
echo "Create cluster certs zipfile..."
$ES_INSTALL_LOCATION/elasticsearch/bin/elasticsearch-certutil cert --silent --pem --in $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/instances.yml --out $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/docker-cluster.zip --ca-cert $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/ca/ca.crt --ca-key $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/ca/ca.key

if [ -d $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/docker-cluster ]; then
    rm -rf $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/docker-cluster
fi

echo "Unzipping cluster certs zipfile..."
unzip $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/docker-cluster.zip -d $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/docker-cluster

# Copy Certificates
mkdir -p $ES_INSTALL_LOCATION/kibana/config/certs
yes | cp -f $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/docker-cluster/kibana/kibana.crt $ES_INSTALL_LOCATION/kibana/config/certs
yes | cp -f $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/docker-cluster/kibana/kibana.key $ES_INSTALL_LOCATION/kibana/config/certs
yes | cp -f $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/ca/ca.crt $ES_INSTALL_LOCATION/kibana/config/certs

# Copy Certificates
mkdir -p $ES_INSTALL_LOCATION/logstash/config/certs
yes | cp -f $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/docker-cluster/logstash/logstash.crt $ES_INSTALL_LOCATION/logstash/config/certs
yes | cp -f $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/docker-cluster/logstash/logstash.key $ES_INSTALL_LOCATION/logstash/config/certs
yes | cp -f $ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/ca/ca.crt $ES_INSTALL_LOCATION/logstash/config/certs


es_url=https://localhost:9200
cacert=$ES_INSTALL_LOCATION/elasticsearch/config/certs/ssl/ca/ca.crt

$parent_dir/elasticsearch/start.sh

# Wait for Elasticsearch to start up.
until curl -s --cacert $cacert -u elastic:${ELASTIC_PASSWORD} $es_url -o /dev/null; do
    sleep 3
    echo "Waiting for Elasticsearch..."
done

# Set the password for the kibana user.
# REF: https://www.elastic.co/guide/en/x-pack/6.0/setting-up-authentication.html#set-built-in-user-passwords
until curl -u elastic:${ELASTIC_PASSWORD} --cacert $cacert -s -H 'Content-Type:application/json' \
     -XPUT $es_url/_xpack/security/user/kibana/_password \
     -d "{\"password\": \"${ELASTIC_PASSWORD}\"}"
     echo "Password for kibana: ${ELASTIC_PASSWORD}"
do
    sleep 2
    echo Failed to set kibana password, retrying...
done

until curl -u elastic:${ELASTIC_PASSWORD} --cacert $cacert -s -H 'Content-Type:application/json' \
     -XPOST $es_url/_security/role/logstash_user \
     -d "{\"cluster\": [\"all\"], \"indices\" : [{ \"names\": [\"flightpax*\",\"case*\"] , \"privileges\": [\"all\"]}]}"
     echo "Password for kibana_user: ${ELASTIC_PASSWORD}"
do
    sleep 2
    echo Failed to set kibana_user password, retrying...
done

until curl -u elastic:${ELASTIC_PASSWORD} --cacert $cacert -s -H 'Content-Type:application/json' \
     -XPUT $es_url/_xpack/security/user/logstash_system/_password \
     -d "{\"password\": \"${ELASTIC_PASSWORD}\"}"
     echo "Password for logstash_system: ${ELASTIC_PASSWORD}"
do
    sleep 2
    echo Failed to set logstash_system password, retrying...
done

until curl -u elastic:${ELASTIC_PASSWORD} --cacert $cacert -s -H 'Content-Type:application/json' \
     -XPOST $es_url/_security/user/logstash_user \
     -d "{\"password\": \"${ELASTIC_PASSWORD}\", \"roles\" : [ \"logstash_user\" ]}"
     echo "Password for kibana_user: ${ELASTIC_PASSWORD}"
do
    sleep 2
    echo Failed to set kibana_user password, retrying...
done

until curl -u elastic:${ELASTIC_PASSWORD} --cacert $cacert -s -H 'Content-Type:application/json' \
     -XPOST $es_url/_security/user/kibana_user \
     -d "{\"password\": \"${ELASTIC_PASSWORD}\", \"roles\" : [ \"kibana_user\" ]}"
     echo "Password for kibana_user: ${ELASTIC_PASSWORD}"
do
    sleep 2
    echo Failed to set kibana_user password, retrying...
done

$parent_dir/kibana/start.sh


# Wait for Elasticsearch to start up.
until curl -f --cacert $cacert -u elastic:${ELASTIC_PASSWORD} https://localhost:5601 -o /dev/null; do
    sleep 3
    echo "Waiting for Kibana..."
done

source $parent_dir/kibana/import-dashboard.sh

$parent_dir/kibana/stop.sh

$parent_dir/elasticsearch/stop.sh
