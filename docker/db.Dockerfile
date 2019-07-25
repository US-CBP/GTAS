FROM mariadb

     ENV MYSQL_DATABASE=gtas \
        MYSQL_ROOT_PASSWORD=admin 
          

COPY docker/init.sql /docker-entrypoint-initdb.d 
COPY gtas-neo4j-etl/sql/neo4j_hit_vw.sql /docker-entrypoint-initdb.d 
COPY gtas-neo4j-etl/sql/neo4j_vw.sql /docker-entrypoint-initdb.d    

EXPOSE 3306
