#!/bin/bash

parent_dir=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd "$parent_dir"

source ./set_env.sh

cd $ES_INSTALL_LOCATION

# Elasticsearch
export ES_HOME=$ES_INSTALL_LOCATION/elasticsearch
export ES_PATH_CONF=$ES_INSTALL_LOCATION/elasticsearch/config

# Kibana
export KIBANA_HOME=$ES_INSTALL_LOCATION/kibana
export KIBANA_PATH_CONF=$ES_INSTALL_LOCATION/kibana/config

#Logstash
export LOGSTASH_HOME=$ES_INSTALL_LOCATION/logstash
export LOGSTASH_PATH_CONF=$ES_INSTALL_LOCATION/logstash/config

# remove existing keystore
if [[ -f $ES_PATH_CONF/elasticsearch.keystore ]]
then
    rm -rf $ES_PATH_CONF/elasticsearch.keystore
fi

# create new keystory
($ES_HOME/bin/elasticsearch-keystore create)

PW=$(openssl rand -base64 16;)
ELASTIC_PASSWORD="${ELASTIC_PASSWORD:-$PW}"
echo $ELASTIC_PASSWORD

echo "Setting password for elastic user"
(echo "$ELASTIC_PASSWORD" | $ES_HOME/bin/elasticsearch-keystore add -x 'bootstrap.password')

# Kibana settings
if [[ -f $KIBANA_HOME/data/kibana.keystore ]] 
then
    echo 'Deleting existing kibana keystore ... '
    rm -rf $KIBANA_HOME/data/kibana.keystore 
fi

echo 'Create new kibana keystore ... '
($KIBANA_HOME/bin/kibana-keystore create)

# echo "Setting password for kibana user"
(echo "$ELASTIC_PASSWORD" | $KIBANA_HOME/bin/kibana-keystore add elasticsearch.password --stdin)
###

# Create keystore if it does not exist
if [[ -f $LOGSTASH_PATH_CONF/logstash.keystore ]] 
then 
    rm -rf $LOGSTASH_PATH_CONF/logstash.keystore
fi
($ES_INSTALL_LOCATION/logstash/bin/logstash-keystore --path.settings $LOGSTASH_PATH_CONF create)

# echo "Setting password for logstash_user user"
(echo "$ELASTIC_PASSWORD" | $ES_INSTALL_LOCATION/logstash/bin/logstash-keystore add ES_PWD)

# # remove old certificate files
# if [[ -d $ES_PATH_CONF/certs ]]
# then 
#     rm -rf $ES_PATH_CONF/certs
    
# Create a folder to contain certificates in the configuration directory of your Elasticsearch node
mkdir -p $ES_PATH_CONF/certs/ssl

yes | cp -f "$parent_dir"/../../config/elasticsearch/instances.yml $ES_PATH_CONF/certs/ssl

# check if old docker-cluster-ca.zip exists, if it does remove and create a new one.
if [ -f $ES_PATH_CONF/certs/ssl/docker-cluster-ca.zip ]; then
    echo "Remove old ca zip..."
    rm $ES_PATH_CONF/certs/ssl/docker-cluster-ca.zip
fi

# Generate a certificate authority for your cluster.
$ES_HOME/bin/elasticsearch-certutil ca --pem --silent --out $ES_PATH_CONF/certs/ssl/docker-cluster-ca.zip

# check if ca directory exists, if does, remove then unzip new files
if [ -d $ES_PATH_CONF/certs/ssl/ca ]; then
    echo "CA directory exists, removing..."
    rm -rf $ES_PATH_CONF/certs/ssl/ca
fi

echo "Unzip ca files..."
unzip $ES_PATH_CONF/certs/ssl/docker-cluster-ca.zip -d $ES_PATH_CONF/certs/ssl

# check if certs zip exist. If it does remove and create a new one.
if [ -f $ES_PATH_CONF/certs/ssl/docker-cluster.zip ]; then
    echo "Remove old docker-cluster.zip zip..."
    rm $ES_PATH_CONF/certs/ssl/docker-cluster.zip
fi
# Generate certificates and private keys
echo "Create cluster certs zipfile..."
$ES_INSTALL_LOCATION/elasticsearch/bin/elasticsearch-certutil cert --silent --pem --in $ES_PATH_CONF/certs/ssl/instances.yml --out $ES_PATH_CONF/certs/ssl/docker-cluster.zip --ca-cert $ES_PATH_CONF/certs/ssl/ca/ca.crt --ca-key $ES_PATH_CONF/certs/ssl/ca/ca.key

if [ -d $ES_PATH_CONF/certs/ssl/docker-cluster ]; then
    rm -rf $ES_PATH_CONF/certs/ssl/docker-cluster
fi

echo "Unzipping cluster certs zipfile..."
unzip $ES_PATH_CONF/certs/ssl/docker-cluster.zip -d $ES_PATH_CONF/certs/ssl/docker-cluster

# Copy Certificates
mkdir -p $KIBANA_PATH_CONF/certs
yes | cp -f $ES_PATH_CONF/certs/ssl/docker-cluster/kibana/kibana.crt $KIBANA_PATH_CONF/certs
yes | cp -f $ES_PATH_CONF/certs/ssl/docker-cluster/kibana/kibana.key $KIBANA_PATH_CONF/certs
yes | cp -f $ES_PATH_CONF/certs/ssl/ca/ca.crt $KIBANA_PATH_CONF/certs

# Copy Certificates
mkdir -p $LOGSTASH_PATH_CONF/certs
yes | cp -f $ES_PATH_CONF/certs/ssl/docker-cluster/logstash/logstash.crt $LOGSTASH_PATH_CONF/certs
yes | cp -f $ES_PATH_CONF/certs/ssl/docker-cluster/logstash/logstash.key $LOGSTASH_PATH_CONF/certs
yes | cp -f $ES_PATH_CONF/certs/ssl/ca/ca.crt $LOGSTASH_PATH_CONF/certs


es_url=https://localhost:9200
cacert=$ES_PATH_CONF/certs/ssl/ca/ca.crt

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

cd $parent_dir

source ./kibana/import-dashboard.sh

$parent_dir/kibana/stop.sh

$parent_dir/elasticsearch/stop.sh
