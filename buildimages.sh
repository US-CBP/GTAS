 sysctl -w vm.max_map_count=262144
# cd gtas
docker-compose -f elk-docker-compose.yml -f neo4j-etl-docker-compose.yml -f docker-compose.yml -f local-deployment.yml down

cd  gtas-parent



docker build -t vtas3:5000/web-app:20231103-1 -f web-app.Dockerfile .
docker build -t vtas3:5000/mariadb:20231103-1 -f mariadb.Dockerfile .
docker build -t vtas3:5000/http-proxy:20231103-1 -f docker-resources/proxy.Dockerfile . 

cd ../

docker-compose -f elk-docker-compose.yml -f neo4j-etl-docker-compose.yml -f docker-compose.yml -f local-deployment.yml up -d