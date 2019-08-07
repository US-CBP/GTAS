FROM maven:3.3-alpine as maven
WORKDIR /etl-project

CMD ["mvn", "install", "-Dskip.unit.tests=true"]