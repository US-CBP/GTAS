# FROM maven:3.3-alpine as build

# WORKDIR /app/

# COPY gtas-parent/pom.xml /app/pom.xml
# COPY gtas-parent/gtas-webapp/pom.xml /app/gtas-webapp/pom.xml
# COPY gtas-parent/gtas-commons/pom.xml /app/gtas-commons/pom.xml
# COPY gtas-parent/gtas-query-builder/pom.xml /app/gtas-query-builder/pom.xml
# COPY gtas-parent/gtas-rulesvc/pom.xml /app/gtas-rulesvc/pom.xml
# COPY gtas-parent/gtas-pdi-parser/pom.xml /app/gtas-pdi-parser/pom.xml
# COPY gtas-parent/gtas-job-scheduler-war/pom.xml /app/gtas-job-scheduler-war/pom.xml
# COPY gtas-parent/gtas-loader/pom.xml /app/gtas-loader/pom.xml
# COPY gtas-parent/gtas-parsers/pom.xml /app/gtas-parsers/pom.xml
# COPY gtas-parent/gtas-commons/src /app/gtas-commons/src

# RUN mvn clean install -Dskip.unit.tests=true --projects gtas-commons -am
# RUN mvn -f /app/gtas-commons/pom.xml hibernate:create  

FROM mariadb

     ENV MYSQL_DATABASE=gtas \
        MYSQL_ROOT_PASSWORD=admin 

COPY docker/init.sql /docker-entrypoint-initdb.d 
COPY gtas-neo4j-etl/sql/neo4j_hit_vw.sql /docker-entrypoint-initdb.d 
COPY gtas-neo4j-etl/sql/neo4j_vw.sql /docker-entrypoint-initdb.d    

EXPOSE 3306
