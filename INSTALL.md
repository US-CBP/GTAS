# GTAS Installation

Please refer to the GTAS Technical Guide document for a more detailed set of installation instructions. 

## Environment

* Java 8
* Apache Tomcat 8
* MariaDB 10.0 Stable
* Maven 3.3
* Apache ActiveMQ 5.15
* Redis 4.0

## Download

GTAS must be build from the source code.  Grab the latest code from GitHub:

```
git clone https://github.com/US-CBP/GTAS.git
```

## Configure

First update the following values in gtas-parent/gtas-commons/src/main/resources/hibernate.properties to work with your installation of MariaDB:

```
hibernate.connection.url=jdbc:mariadb://localhost:3306/gtas
hibernate.connection.username=root
hibernate.connection.password=admin
```

Configure the job scheduler war by editing gtas-parent/gtas-job-scheduler-war/src/main/resources/jobScheduler.properties.  Modify the message origin and processed folders.  For example,

```
message.dir.origin=/data/gtas_in
message.dir.processed=/data/gtas_out
```

Repeat this process for all properties files that set set directories for messages received, and messages processed.

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

## Deploy

Create the schema, load application configuration and lookup data using Maven:

```
cd gtas-commons
mvn hibernate4:export
```

Deploy to tomcat and start the server.

```
cp gtas-webapp/target/gtas.war [tomcat home]/webapps
cp gtas-job-scheduler-war/target/gtas-job-scheduler.war [tomcat home]/webapps
```

Access site at http://localhost:8080/gtas

## Backend Processes 

These instructions are only for admins who wish to configure the backend processes as cron jobs on a Unix system.  If you plan on using the job scheduler war instead, there is no need to follow these instructions.

GTAS currently relies on the following batch processes:

* GTAS Loader: Parses and loads APIS and PNR messages.
* GTAS Rule Runner: Applies user-defined rules against messages.

Both of these processes can be executed on the command-line.

### GTAS Loader (these have been temporarily disabled as all message loading must be handled by Job Scheduler)

After compiling, the GTAS loader is located in gtas-loader/target/gtas-loader.jar.  It's an executable jar that takes one or more input filenames on the command line.  For example, the following command would execute the loader on two input files:

```
java -jar gtas-loader/target/gtas-loader.jar 101.txt 102.txt
```

Alternatively, you can provide input and output directories.  This is useful for setting up the loader as a batch process, and in cases where the shell may not be able to handle a large number of input files:

```
java -jar gtas-loader/target/gtas-loader.jar inputdir outputdir
```

### GTAS Rule Runner (this has been temporarily disabled as rules engine must be handled by Job Scheduler)

The rule runner takes no arguments

```
java -jar gtas-rulesvc/target/gtas-rulesvc-jar-with-dependencies.jar 
```
