curl -L -O https://artifacts.elastic.co/downloads/logstash/logstash-7.2.0.tar.gz
tar -xzvf logstash-7.2.0.tar.gz

./logstash-7.2.0/bin/logstash-keystore create
echo $(cat mysql_logstash_user.txt) | ./logstash-7.2.0/bin/logstash-keystore add MARIADB_USER
echo $(cat mysql_logstash_password.txt) | ./logstash-7.2.0/bin/logstash-keystore add MARIADB_PASSWORD
echo $(cat elastic_user.txt) | ./logstash-7.2.0/bin/logstash-keystore add ELASTIC_USER
echo $(cat elastic_password.txt) | ./logstash-7.2.0/bin/logstash-keystore add ELASTIC_PASSWORD

cp ./logstash-7.2.0/config/logstash.keystore ./

./logstash-7.2.0/bin/logstash-keystore list

rm -rf logstash-7.2.0 && rm -rf logstash-7.2.0.tar.gz
