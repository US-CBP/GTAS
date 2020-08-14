FROM centos:7
MAINTAINER The CentOS Project <cloud-ops@centos.org>
LABEL Vendor="CentOS" \
      License=GPLv2 \
      Version=2.4.6-40

RUN yum -y --setopt=tsflags=nodocs update && \
    yum -y --setopt=tsflags=nodocs install java-1.8.0-openjdk && \
    yum -y --setopt=tsflags=nodocs install java-1.8.0-openjdk-devel && \
    yum -y --setopt=tsflags=nodocs group install "Development Tools" && \
    yum -y --setopt=tsflags=nodocs install sudo -y && \
    yum -y --setopt=tsflags=nodocs install lsof && \
    yum -y --setopt=tsflags=nodocs install initscripts && \
    yum -y --setopt=tsflags=nodocs install net-tools python-setuptools python3-setuptools python3-pip python3-devel && \
    yum -y --setopt=tsflags=nodocs install openssh-server openssh-clients passwd && \
    curl -sL https://rpm.nodesource.com/setup_10.x | sudo bash - && \
    yum -y --setopt=tsflags=nodocs install nodejs && \
    yum -y --setopt=tsflags=nodocs install  gcc-c++ make && \
    yum clean all && \
    easy_install supervisor

###################### BEGIN SSHD SETTINGS #############################################

RUN mkdir /var/run/sshd

RUN ssh-keygen -t rsa -f /etc/ssh/ssh_host_rsa_key -N ''

RUN /usr/bin/ssh-keygen -A

ADD start_ssh.sh /start_ssh.sh
RUN chmod -v +x /start_ssh.sh

# SSH port
EXPOSE 22
EXPOSE 222

###################### END SSHD SETTINGS ###############################################

###################### BEGIN KAIZEN SERVER SETTINGS #####################################

#RUN mkdir -p /var/log/supervisord

RUN echo alias python="/usr/bin/python3.6" >> ~/.bash_profile

ADD supervisord.conf /etc/supervisor/conf.d/supervisord.conf

RUN mkdir -p /var/log/supervisord

RUN mkdir -p /var/log/omni

RUN mkdir -p /opt/omni/bin

RUN mkdir -p /opt/omni/certs

RUN mkdir -p /opt/omni/.model_repository

RUN mkdir -p /opt/omni/.model_repository/v0

RUN mkdir -p /opt/omni/.training_ground

RUN mkdir -p /opt/omni/.training_ground/scala

RUN mkdir -p /opt/omni/.model_performance_metrics

RUN mkdir -p /opt/omni/.model_predictions

RUN mkdir -p /opt/omni/.testing_center

RUN mkdir -p /opt/omni/.testing_center/results

RUN mkdir -p /opt/omni/.testing_center/requests

ENV OMNI_HOME /opt/omni

RUN export OMNI_HOME

RUN mkdir -p /opt/omni/.ivy2

ADD install_scala_kaizen_model_builder.run /opt/omni/bin/install_scala_kaizen_model_builder.run
RUN chmod -v +x /opt/omni/bin/install_scala_kaizen_model_builder.run

ADD install_java_kaizen_model_builder.run /opt/omni/bin/install_java_kaizen_model_builder.run
RUN chmod -v +x /opt/omni/bin/install_java_kaizen_model_builder.run

ADD build_kaizen_model.sh /opt/omni/bin/build_kaizen_model.sh
RUN chmod -v +x /opt/omni/bin/build_kaizen_model.sh

ADD evaluate_kaizen_model.sh /opt/omni/bin/evaluate_kaizen_model.sh
RUN chmod -v +x /opt/omni/bin/evaluate_kaizen_model.sh

ADD run_kaizen_model_predictions.sh /opt/omni/bin/run_kaizen_model_predictions.sh
RUN chmod -v +x /opt/omni/bin/run_kaizen_model_predictions.sh

ADD init_kaizen_model_deployment.sh /opt/omni/bin/init_kaizen_model_deployment.sh
RUN chmod -v +x /opt/omni/bin/init_kaizen_model_deployment.sh

ADD cancel_kaizen_model_training.sh /opt/omni/bin/cancel_kaizen_model_training.sh
RUN chmod -v +x /opt/omni/bin/cancel_kaizen_model_training.sh

ADD run_kaizen.sh /run_kaizen.sh
RUN chmod -v +x /run_kaizen.sh

# Generic Server Configuration
RUN export KAIZEN_PRIVATE_IP_ADDRESS
RUN export KAIZEN_PUBLIC_IP_ADDRESS
RUN export KAIZEN_WEBSERVER_PORT
RUN export KAIZEN_HTTP_SCHEME

# EMAIL Configuration
RUN export KAIZEN_SERVER_EMAIL_SENDER_USERNAME
RUN export KAIZEN_SERVER_EMAIL_SENDER_PASSWORD

# SSL Certs Configuration
ADD kaizen_ca.p12 /opt/omni/certs/kaizen_ca.p12
RUN chmod 640 /opt/omni/certs/kaizen_ca.p12
RUN export KAIZEN_EXTERNAL_KEYSTORE
RUN export KAIZEN_EXTERNAL_KEYSTORE_PASSWORD
RUN export KAIZEN_EXTERNAL_KEYSTORE_TYPE
RUN export KAIZEN_EXTERNAL_KEY_ALIAS

RUN export KAIZEN_SUPPORT_PHONE

# MYSQL Configuration
RUN export KAIZEN_SERVER_MYSQL_DB_HOSTNAME
RUN export KAIZEN_SERVER_MYSQL_DB_PORT
RUN export KAIZEN_SERVER_MYSQL_DB_USERNAME
RUN export KAIZEN_SERVER_MYSQL_DB_PASSWORD
RUN export KAIZEN_SERVER_MYSQL_DB_DATABASE_NAME
RUN export KAIZEN_SERVER_MYSQL_DB_DATASOURCE

# SYSTEM MODE Configuration
RUN export KAIZEN_SERVER_DEVELOPMENT_MODE

RUN export KAIZEN_SPRING_PROFILES_ACTIVE

ADD model-manager-1.0.0.jar /opt/omni/model-manager-1.0.0.jar

RUN mkdir -p /opt/omni/.dataset_external_repository

RUN mkdir -p /opt/omni/.dataset_internal_repository

ADD OS_FLIGHT_LKOUT.dat /opt/omni/.dataset_internal_repository

ENV KAIZEN_SERVER_DATA_REPOSITORY_FOLDER /opt/omni/.dataset_external_repository
RUN export KAIZEN_SERVER_DATA_REPOSITORY_FOLDER

ENV KAIZEN_SERVER_DATA_REPOSITORY_TYPE filesystem

RUN export KAIZEN_SERVER_DATA_REPOSITORY_TYPE

RUN mkdir -p /opt/omni/.model_repository

ENV KAIZEN_SERVER_MODEL_REPOSITORY_FOLDER /opt/omni/.model_repository
RUN export KAIZEN_SERVER_MODEL_REPOSITORY_FOLDER

ENV PROFILE_MANAGER_MODEL_REPOSITORY_FOLDER /opt/omni/.model_repository
RUN export PROFILE_MANAGER_MODEL_REPOSITORY_FOLDER

# ACTIVEMQ Configuration
RUN export ACTIVEMQ_HOSTNAME
RUN export ACTIVEMQ_PORT
RUN export ACTIVEMQ_BROKER_URL
RUN export ACTIVEMQ_BROKER_USERNAME
RUN export ACTIVEMQ_BROKER_PASSWORD

# HTTP port
EXPOSE 9081

# HTTPS port
EXPOSE 9443

# WEBSOCKET Configuration
RUN export WEBSOCKET_SERVER_DOMAIN
RUN export WEBSOCKET_SERVER_PORT
RUN export WEBSOCKET_SERVER_PROTOCOL_SCHEME

# SSHD runs within the context of KAIZEN container so start it...
RUN ./start_ssh.sh

###################### END KAIZEN SERVER SETTINGS #########################################

###################### BEGIN JOB SERVER SETTINGS ########################################

RUN echo "JAVA_HOME=$(readlink -f /usr/bin/java | sed "s:bin/java::")" | sudo tee -a ~/.bash_profile

RUN source ~/.bash_profile

# Install spark
ADD \
  spark*.tgz /opt/omni/spark
RUN \
  mv /opt/omni/spark/*/* /opt/omni/spark && \
  rmdir /opt/omni/spark/spark*
ADD\
    spark-defaults.conf /opt/omni/spark/conf/spark-defaults.conf
ADD \
  log4j.properties.spark /opt/omni/spark/conf/log4j.properties
ENV \
  SPARK_HOME /opt/omni/spark
RUN \
  export SPARK_HOME

# Install livy
ADD \
  livy*.zip /opt/omni/livy.zip

RUN \
  cd /opt/omni && \
  unzip livy.zip && \
  rm livy.zip && \
  mv livy* livy

ADD \
  livy.conf /opt/omni/livy/conf/livy.conf
ADD \
  log4j.properties.livy /opt/omni/livy/conf/log4j.properties

# Place jars for the spark session
ADD \
  pax-modeling.jar /opt/omni/livy/rsc-jars/pax-modeling.jar

RUN \
  mkdir -p /var/log/omni

ADD \
  run_livy.sh /opt/omni/run_livy.sh
RUN \
  chmod +x /opt/omni/run_livy.sh

RUN \
  mkdir -p /opt/omni/models

ADD isoforest.model /opt/omni/models/isoforest.model
ADD isoforest.model /opt/omni/.model_repository/v0/isoforest.model

ADD supervised.model /opt/omni/models/supervised.model
ADD supervised.model /opt/omni/.model_repository/v0/supervised.model

ADD supervisedFeatures.model /opt/omni/models/supervisedFeatures.model
ADD supervisedFeatures.model /opt/omni/.model_repository/v0/supervisedFeatures.model

###################### END JOB SERVER SETTINGS ########################################

ENTRYPOINT ["/usr/bin/supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]
