FROM adoptopenjdk/maven-openjdk8 as build-db

RUN mkdir /init-scripts/
COPY ./gtas-commons/secrets/init/ /init-scripts/
RUN apt-get update && apt-get -y install dos2unix && dos2unix /init-scripts/a-mariadb-user-creation-script.sh


COPY ./ /gtas-parent/
WORKDIR /gtas-parent/gtas-commons
RUN mvn clean install -Dmaven.test.skip=true
RUN mvn hibernate:create -Dhibernate.schema.execute=false

FROM mariadb:10.4

COPY --from=build-db /gtas-parent/gtas-commons/target/create.sql /docker-entrypoint-initdb.d/b-create.sql
COPY --from=build-db /init-scripts/ /docker-entrypoint-initdb.d/

ENV MYSQL_DATABASE=gtas

CMD ["--character-set-server=utf8", "--collation-server=utf8_general_ci", "--max_allowed_packet=32505856"]