# GTAS Installation

Please refer to the GTAS Technical Guide document for a more detailed set of installation instructions or, check out our youtube playlist that walks you through the whole process:

https://www.youtube.com/playlist?list=PLrB3ZYSlISMA5aNaOhxhvqjoNHTjZIfkK

## Environment

* Java 8
* Apache Tomcat 8
* MariaDB 10.0 Stable
* Maven 3.3
* Apache ActiveMQ 5.15

## Download Source Code

Any branch of GTAS can be built from source code, or you can download the .war files from the latest stable release. 
Grab the latest code from the 'dev' branch GitHub:
```
git clone --branch dev --single-branch https://github.com/US-CBP/GTAS.git
```

OR download the master branch.
```
git clone https://github.com/US-CBP/GTAS.git
```

## Build

Standard build with unit tests. 
Note: if maven shows a java socket error (invalid argument 'connect'), 
you may need to add -Djava.net.preferIPv4Stack=true to the MAVEN_OPTS environment variable.
```
cd gtas-parent
mvn clean install
```

Build without unit tests
```
mvn clean install -Dskip.unit.tests=true
```

Build with integration tests (and unit tests).  Requires setting up the database fully.  See below.
```
mvn clean install -Dskip.integration.tests=false
```

## Configure & Deploy

### MariaDB Database

Create the database schema, load application configuration and lookup data using Maven:

Log in to MariaDB and run the following command to create the 'gtas' database:
```
MariaDB [(none)]> CREATE DATABASE IF NOT EXISTS gtas CHARACTER SET utf8 COLLATE utf8_general_ci;
```

#### Adding the schema to the database

Note that in order to run on jdk11, the code uses hibernate-maven-plugin:2.1.2-SNAPSHOT from
https://github.com/neilireson/hibernate-maven-plugin.git, this has to be installed before the following will work.
 
To script the database, run the following commands
```
cd gtas-commons
mvn hibernate:update
mvn hibernate:drop
mvn hibernate:create
```

### Initialise ELK services

Install all ELK stack (Installs Elasticsearch, Logstash, Kibana and configures self-signed certificates)

#### Mac
```
./gtas-parent/scripts/elastic/install/mac/install_all.sh
./gtas-parent/scripts/elastic/install/mac/start_all.sh
```
#### Linux
```
./gtas-parent/scripts/elastic/install/linux/install_all.sh
./gtas-parent/scripts/elastic/install/linux/start_all.sh
```

The ELK [random generated password] is created during the installation process by the setup-security.sh script

Elasticsearch:
user: elastic
password: [random generated password]

Kibana:
user: kibana_user
password: [random generated password]

### Install and start ActiveMQ

Install ActiveMQ (https://activemq.apache.org/getting-started)
```
activemq start
```

### Install and start Neo4J

#### Mac
```
./gtas-parent/scripts/graph_db/mac/install_and_start_all.sh
```

#### Linux
```
./gtas-parent/scripts/graph_db/linux/install_and_start_all.sh
```

Default Neo4j:
user: neo4j
password: neo4j

When logging Neo4j with the above credentials it will ask for a new password. 
Change the password to the GTAS one:

password: password

### Download and configure Tomcat, and Deploy the WAR files

Download Tomcat (9) https://tomcat.apache.org/download-90.cgi

Deploy to tomcat, update application.properties file, and start the server.

You will need to create two folders for processing messages in a development environment; 
one for unprocessed messages and one for messages that have been loaded by GTAS. 
You may name the files as you wish, but the names must correspond with those in the application.properties file.
This file must be copied to the configuration directory before starting the server.
```
cp ./gtas-parent/gtas-commons/src/main/resources/default.application.properties [TOMCAT_HOME]/conf
```

Move the .war files to the tomcat server
```
cp ./gtas-parent/gtas-webapp/target/gtas.war [TOMCAT_HOME]/webapps
cp ./gtas-parent/gtas-job-scheduler-war/target/gtas-job-scheduler.war [TOMCAT_HOME]/webapps
```

### Start the tomcat server
```
[TOMCAT_HOME]/bin/catalina.sh run
```

### Open interface in a browser

Access site at http://localhost:8080/gtas

The default login is:

user: admin
password: password

## Issues

The main GTAS attempts to open the Kibana app but responds with a "HTTP Status 404 – Not Found" message.
"The requested resource [/app/kibana] is not available"
It is possible to log into Kibana, I think this may be a password issue. 
The password is created during the install_all.sh processing
