FROM adoptopenjdk/maven-openjdk8 as mvn

RUN apt-get -y update
RUN apt-get -y install nodejs
RUN apt-get -y install git

COPY entrypoint.sh entrypoint.sh
COPY default.application.properties default.application.properties
COPY hibernate.properties hibernate.properties

VOLUME /root/.m2

ENTRYPOINT sh entrypoint.sh