FROM adoptopenjdk/maven-openjdk8


COPY ./gtas-parent/ /gtas-parent
WORKDIR /gtas-parent
RUN mvn clean install --projects gtas-loader-runner -am

WORKDIR /gtas-parent/gtas-loader-runner
ENV RUN_ARGUMENTS ' --loader.name=${LOADER_NAME} \
                    --loader.country=${LOADER_COUNTRY} \
                    --loader.permits=${LOADER_PERMITS} \
                    --message.dir.origin=${INBOUND_MESSAGE_FOLDER} \
                    --message.dir.working=${WORKING_MESSAGE_FOLDER} \
                    --message.dir.processed=${PROCESSED_MESSAGE_FOLDER} \
                    --message.dir.error=${ERROR_MESSAGE_FOLDER} \
                    --spring.datasource.url=jdbc:mysql://${MARIA_URL}:3306/gtas \
                    --spring.datasource.username=${MARIA_USERNAME} \
                    --spring.datasource.password=${MARIA_PASSWORD} \
                    --spring.activemq.broker-url=tcp://${ACTIVE_MQ_HOST}:61616'

RUN mkdir -p /usr/local/gtas-data/processed /usr/local/gtas-data/error /usr/local/gtas-data/input /usr/local/gtas-data/working

RUN mvn spring-boot:run -Dspring-boot.run.skip=true
CMD mvn spring-boot:run -Dspring-boot.run.arguments="$RUN_ARGUMENTS" -Dspring-boot.run.jvmArguments="$JVM_ARGS"