FROM adoptopenjdk/maven-openjdk8 as build-stage

RUN apt-get -y update && apt-get -y install nodejs && apt-get -y install git

COPY ./ /gtas-parent/
COPY ./docker-resources/hibernate.properties /gtas-parent/gtas-commons/src/main/resources/hibernate.properties

WORKDIR /gtas-parent
RUN mvn clean install -Dmaven.test.skip=true
RUN cd / && rm -rf /gtas-parent

FROM tomcat:9-jdk8-adoptopenjdk-openj9 as tomcat

RUN mkdir /usr/local/tomcat/webapps/gtas
COPY --from=build-stage /root/.m2/repository/gov/gtas/gtas-webapp/1.0.0-BUILD-SNAPSHOT/gtas-webapp-1.0.0-BUILD-SNAPSHOT.war /usr/local/tomcat/webapps/gtas.war
COPY ./docker-resources/default.application.properties /usr/local/tomcat/conf/application.properties
COPY ./docker-resources/setenv.sh /usr/local/tomcat/bin/setenv.sh

WORKDIR /usr/local/tomcat/webapps/gtas
RUN  jar -xvf /usr/local/tomcat/webapps/gtas.war
WORKDIR /usr/local/tomcat/bin
CMD ["catalina.sh", "run"]

# <<<<<<< HEAD

# COPY ./docker-resources/building-script.sh /building-script.sh
# COPY ./docker-resources/hibernate.properties /hibernate.properties
# =======
# RUN apt-get -y update && apt-get -y install nodejs && apt-get -y install git
# >>>>>>> 1342f5f1fd09d4a6e598608f4c67028565a47f82
# COPY ./ /gtas-parent/
# COPY ./docker-resources/default.application.properties /gtas-parent/gtas-commons/src/main/resources/default.application.properties
# COPY ./docker-resources/hibernate.properties /gtas-parent/gtas-commons/src/main/resources/hibernate.properties

# <<<<<<< HEAD
# RUN apt-get -y update && apt-get -y install nodejs && apt-get -y install git && sh /building-script.sh



# FROM tomcat:9-jdk8-adoptopenjdk-openj9 as tomcat

# COPY --from=build-stage /root/.m2/repository/gov/gtas/gtas-webapp/1.0.0-BUILD-SNAPSHOT/gtas-webapp-1.0.0-BUILD-SNAPSHOT.war /gtas-webapp-1.0.0-BUILD-SNAPSHOT.war
# COPY ./docker-resources/default.application.properties /usr/local/tomcat/conf/application.properties
# COPY ./docker-resources/setenv.sh /usr/local/tomcat/bin/setenv.sh
# COPY ./entrypoint.sh /entrypoint.sh

# ENTRYPOINT sh /entrypoint.sh  
# =======
# WORKDIR /gtas-parent
# RUN mvn clean install -Dmaven.test.skip=true
# RUN cd / && rm -rf /gtas-parent

# FROM tomcat:9-jdk8-adoptopenjdk-openj9 as tomcat
# RUN mkdir /usr/local/tomcat/webapps/gtas
# COPY --from=build-stage /root/.m2/repository/gov/gtas/gtas-webapp/1.0.0-BUILD-SNAPSHOT/gtas-webapp-1.0.0-BUILD-SNAPSHOT.war /usr/local/tomcat/webapps/gtas.war
# WORKDIR /usr/local/tomcat/webapps/gtas
# RUN  jar -xvf /usr/local/tomcat/webapps/gtas.war
# WORKDIR /usr/local/tomcat/bin
# CMD ["catalina.sh", "run"]
# >>>>>>> 1342f5f1fd09d4a6e598608f4c67028565a47f82
