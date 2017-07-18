#FROM mariadb:10.3
FROM mysql:8.0
ENV MYSQL_DATABASE=gtas \
    MYSQL_ROOT_PASSWORD=admin\
    #MYSQL_USER=root \
    MYSQL_PASSWORD=admin

# RUN echo "DROP DATABASE IF EXISTS gtas;\
# CREATE DATABASE gtas;\
# USE gtas;" > init_sql.sql && cp init_sql.sql /docker-entrypoint-initdb.d
RUN rm -rf /docker-entrypoint-initdb.d
# RUN rm -rf /var/lib/mysql/ib_logfile*
# RUN sed -e 's/user._=._mysql/user=root/' -i /etc/mysql/my.cnf

EXPOSE 3306
#CMD ["docker-entrypoint.sh"]
