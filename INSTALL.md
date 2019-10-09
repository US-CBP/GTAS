# GTAS Installation

Please refer to the GTAS Technical Guide document for a more detailed set of installation instructions or, check out our youtube playlist that walks you through the whole process:

https://www.youtube.com/watch?v=1h626On3Tto&t=18s

## Environment

* Java 8
* Apache Tomcat 8
* MariaDB 10.0 Stable
* Maven 3.3
* Apache ActiveMQ 5.15

## Download Source Code

Any branch of GTAS can be built from source code, or you can download the .war files from the latest stable release. Grab the latest code from the 'dev' branch GitHub:

```
git clone --branch dev --single-branch https://github.com/US-CBP/GTAS.git
```

OR download the master branch.

```
git clone https://github.com/US-CBP/GTAS.git
```

## Build

Standard build with unit tests. Note: if maven shows a java socket error (invalid argument 'connect'), you may need to add -Djava.net.preferIPv4Stack=true to the MAVEN_OPTS environment variable.

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

Create the database schema, load application configuration and lookup data using Maven:

Log in to MariaDB and run the following command to create the 'gtas' database:

```
MariaDB [(none)]> CREATE DATABASE IF NOT EXISTS gtas CHARACTER SET utf8 COLLATE utf8_general_ci;
```

To script the database, run the following commands

```
cd gtas-commons
mvn hibernate:update
mvn hibernate:drop
mvn hibernate:create
```

Deploy to tomcat, update application.properties file, and start the server.

You will need to create two folders for processing messages in a development environment; one for unprocessed messages and one for messages that have been loaded by GTAS. You may name the files as you wish, but the names must correspond with those in the application.properties file. This file can be located in the directory below:

~/GTAS/gtas-parent/gtas-commons/src/main/resources/default.application.properties

This file must be moved to the ~/usr/local/apache-tomcat-9.0.22/conf directory before starting the server to take effect.

Move the .war files to the tomcat server

```
cp gtas-webapp/target/gtas.war [tomcat home]/webapps
cp gtas-job-scheduler-war/target/gtas-job-scheduler.war [tomcat home]/webapps
```

Start the server

```
./usr/local/apache-tomcat-9.0.22/bin/catalina.sh run
```

Access site at http://localhost:8080/gtas

The default login is:

user: admin
password: password
