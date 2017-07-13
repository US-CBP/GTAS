FROM mariadb:10.0
ENV MYSQL_DATABASE=gtas \
    MYSQL_ROOT_PASSWORD=admin\
    # MYSQL_USER=root \
    MYSQL_PASSWORD=admin

#ADD gtas-parent/gtas-commons/target/schema.sql /docker-entrypoint-initdb.d
ADD target/dev_to_sandbox.sql /docker-entrypoint-initdb.d
#ADD src/main/resources/sql/countries.sql /docker-entrypoint-initdb.d
#ADD src/main/resources/sql/carriers.sql /docker-entrypoint-initdb.d
#ADD src/main/resources/sql/airports.sql /docker-entrypoint-initdb.d
ADD src/main/resources/sql/gtas_data.sql /docker-entrypoint-initdb.d
ADD src/main/resources/sql/views.sql /docker-entrypoint-initdb.d

EXPOSE 3306
#ENTRYPOINT "/usr/sbin/mysqld"
#CMD ["/bin/bash", "docker-entrypoint.sh"]
