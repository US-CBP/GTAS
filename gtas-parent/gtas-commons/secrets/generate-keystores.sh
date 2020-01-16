curl -L -O https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-7.2.0-darwin-x86_64.tar.gz
tar -xzvf elasticsearch-7.2.0-darwin-x86_64.tar.gz

./elasticsearch-7.2.0/bin/elasticsearch-keystore create

cp ./logstash-7.2.0/config/logstash.keystore ./

./logstash-7.2.0/bin/logstash-keystore list

rm -rf logstash-7.2.0 && rm -rf logstash-7.2.0.tar.gz
