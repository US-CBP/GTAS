FROM maven:latest

RUN mkdir -p /project

VOLUME gtas-parent /project
COPY gtas-parent /project
WORKDIR /project

RUN mvn clean install -D skip.unit.tests=true
# \
#     && cd gtas-commons\
#     && mvn hibernate4:export
#
# CMD ["mvn", "clean", "install", "-D skip.unit.tests=true"]

# FROM mariadb:10.0
# ENV MYSQL_DATABASE=gtas \
#     MYSQL_ROOT_PASSWORD=admin
#
# #ADD ./gtas-commons/target/schema.sql /docker-entrypoint-initdb.d
#
# RUN cd gtas-commons\
#     && mvn hibernate4:export

CMD ["mvn", "clean", "install", "-D skip.unit.tests=true"]
