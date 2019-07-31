FROM java:8-jre

# Set required environment vars
ENV PDI_VERSION=8.2.0.3-519 \
    CARTE_PORT=8181 \
    PENTAHO_JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64 \
    PENTAHO_HOME=/home/pentaho

# Create user
RUN mkdir ${PENTAHO_HOME} && \
    groupadd -r pentaho && \
    useradd -s /bin/bash -d ${PENTAHO_HOME} -r -g pentaho pentaho && \
    chown pentaho:pentaho ${PENTAHO_HOME}



# Assign the ownership of $PENTAHO_HOME
RUN chown -R pentaho:pentaho $PENTAHO_HOME 
    # chmod -R 755 chown -R pentaho:pentaho $PENTAHO_HOME

# Download PDI
RUN /usr/bin/wget \
    --progress=dot:giga \
    https://s3.amazonaws.com/kettle-neo4j/kettle-neo4j-remix-${PDI_VERSION}-REMIX.zip \
    -O /tmp/pdi-ce-${PDI_VERSION}.zip && \
    /usr/bin/unzip -q /tmp/pdi-ce-${PDI_VERSION}.zip -d  $PENTAHO_HOME && \
    rm /tmp/pdi-ce-${PDI_VERSION}.zip

 COPY ./drivers/mariadb-java-client-2.2.1.jar $PENTAHO_HOME/data-integration/lib

# Reassign the ownership of $PENTAHO_HOME
RUN chown -R pentaho:pentaho $PENTAHO_HOME

COPY ./pdi-conf/.pentaho /home
# Switch to the pentaho user
USER pentaho

# We can only add KETTLE_HOME to the PATH variable now
# as the path gets eveluated - so it must already exist
# ENV KETTLE_HOME=$PENTAHO_HOME/data-integration \
#     PATH=$KETTLE_HOME:$PATH

# Expose Carte Server
EXPOSE ${CARTE_PORT}

