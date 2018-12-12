
FROM mariadb

ENV MYSQL_DATABASE=gtas \
    MYSQL_ROOT_PASSWORD=admin\
    #MYSQL_USER=root \
    MYSQL_PASSWORD=admin


COPY init.sql /docker-entrypoint-initdb.d
COPY init_privileges.sql /docker-entrypoint-initdb.d

EXPOSE 3306
