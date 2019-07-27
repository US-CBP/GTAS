FROM maven:latest

# VOLUME [ "/project" ]
# COPY gtas-parent /project
WORKDIR /project

RUN rm -fr /root/.m2/repository/gov/gtas/gtas-webapp

CMD ["mvn", "install", "-Dskip.unit.tests=true"]
