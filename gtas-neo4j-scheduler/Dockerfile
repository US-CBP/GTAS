FROM maven:3.3-alpine as maven
WORKDIR /app
COPY ./pom.xml /app/pom.xml
COPY ./src /app/src

RUN mvn clean install '-Dmaven.exec.skip=true' -Dskip.unit.tests=true