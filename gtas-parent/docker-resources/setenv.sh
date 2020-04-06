export DB_USERNAME=$(cat /run/secrets/mysql_webapp_user)
export DB_PASSWORD=$(cat /run/secrets/mysql_webapp_password)
export NEO4J_USERNAME=$(cat /run/secrets/webapp_neo4j_user)
export NEO4J_PASSWORD=$(cat /run/secrets/webapp_neo4j_password)
export ELASTIC_USERNAME=elastic
export ELASTIC_PASSWORD=$(cat /run/secrets/elastic_bootstrap_password)
export ELASTICSEARCH_HOST=elasticsearch
