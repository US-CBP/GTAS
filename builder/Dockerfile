FROM adoptopenjdk/maven-openjdk8 as mvn

RUN mkdir service_status && echo started > service_status/builder_status.txt
VOLUME service_status

RUN apt-get -y update || continue && apt-get -y install nodejs && apt-get -y install git

COPY ./entrypoint.sh /entrypoint.sh
COPY ./default.application.properties /default.application.properties
COPY ./hibernate.properties /hibernate.properties

VOLUME /root/.m2

ENTRYPOINT sh entrypoint.sh && echo completed > echo builder_status.txt || echo failed > builder_status.txt