

# Docker Instructions
----------

## Prerequisites

- Install Docker and Docker Compose - [Windows](https://docs.docker.com/docker-for-windows/install/), [Linux (Centos)](https://https://docs.docker.com/install/linux/docker-ce/centos/) and [Mac](https://docs.docker.com/docker-for-mac/install/)

```sh
# The command below should display installed docker version
$ docker version
Client: Docker Engine - Community
 Version:           18.09.2

# The command below should display installed docker-compose version
$ docker-compose version
docker-compose version 1.23.2, build 1110ad0
```

- Download the latest source code

```bash
$ git clone --branch dev --single-branch https://github.com/US-CBP/GTAS.git

$ cd GTAS # All docker commands below should be executed inside this directory
```

## Configure Local Folders

By default, GTAS will read messages from `__data/gtas_in` and archive them in `__data/gtas_out` after parsing/loading it. The folders location can be updated in the `.env` file.

```properties
LOCAL_DRIVE_MAPPING_INPUT=<INPUT_ABSOLUTE_PATH>
LOCAL_DRIVE_MAPPING_OUTPUT=<OUTPUT_ABSOLUTE_PATH>
```

## Build and Run GTAS


### Run WebApp and Scheduler

From under  the top directory, issue the command below.

``` bash
docker-compose up -d
```

This will spin up containers:

- `webapp` - <http://localhost:8080/gtas>

- `scheduler`

- `mariahost` - Connection: (host=localhost, username=root, password=admin)

- `activemq` - <http://localhost:8161> Login (Username=admin, Password=admin)

- `elasticsearch` - <http://localhost:9200>

- `logstash`

- `kibana` - <http://localhost:5601>

### Run web applicaton only

From under the top directory, issue the command below.

``` bash
docker-compose up -d webapp
```

This will spin up containers:

- `webapp` - <http://localhost:8080/gtas>

- `mariahost` - Connection: (host=localhost, username=root, password=admin)

- `elasticsearch` - <http://localhost:9200>

- `kibana` - <http://localhost:5601>

### Run The Scheduler (Parser/Loader) only

``` bash
docker-compose up -d scheduler
```

This will spin up containers:

- `scheduler`

- `mariahost` - Connection: (host=localhost, username=root, password=admin)

- `activemq` - <http://localhost:8161> Login (Username=admin, Password=admin)

- `elasticsearch` - <http://localhost:9200>

- `logstash`

- `kibana` - <http://localhost:5601>

### Run all services except tomcat

``` bash
docker-compose up -d mariahost elasticsearch activemq  kibana logstash
```

This will spin up containers:

- `mariahost` - Connecttion: (host=localhost, username=root, password=admin)

- `activemq` - <http://localhost:8161> Login (Username=admin, Password=admin)

- `elasticsearch` - <http://localhost:9200>

- `logstash`

- `kibana` - <http://localhost:5601>
