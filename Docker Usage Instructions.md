

# Docker Instructions
----------
 **Installation**
- Download and Install **Docker Toolbox** for Windows 	
					[Docker Toolbox](https://docs.docker.com/toolbox/toolbox_install_windows/)
- Check whether you have everything up and running properly 

```sh
$ docker version
```

**Dec 2018** update - 
  _**Option1:**_  Type in individual Docker commands

Tagged images of GTAS are available on Docker Hub - run these commands to start testing GTAS in your local env.

```
$ docker network create gtas_default

$ docker container run -d --name mariahost -p 3306:3306 --network gtas_default sanandreas/gtas_mariadb:v3

$ docker container run -d --name redis -p 6379:6379 --network gtas_default redis

$ docker container run -d --name activemq -p 61616:61616 -p 8161:8161 --network gtas_default rmohr/activemq


```
Update this following command with your local volume mounts (place where you drop files to be parsed) to help the application pick API/PNR files from your local hard drive.

Replace this < your local folder> with your local mappings
```
$ docker container run -d --name tomcat -p 8080:8080 --network gtas_default -v //c/db/input: < your local input folder> -v //c/db/output: < your local output folder> sanandreas/gtas_tomcat:v3
```

 _**Option2:**_  Let   __**docker-compose**__    handle the process of building and deploying
		
- From under  <**gtas-parent/docker**> folder,  look up this file
 *docker-compose.yml*
 - Run these two following lines to update docker with your local file structure for input and output API/ PNR files - **change the Right Hand Side with your local file system values** 
 - Ex:  LOCAL_DRIVE_MAPPING_INPUT=<your_local_path>
 
 - Note that for Windows file system - we use two forward slashes //
 and for Linux/Unix/Mac file system - we use single forward slash /
 ```
 echo LOCAL_DRIVE_MAPPING_INPUT=//C/Message > .env
 echo LOCAL_DRIVE_MAPPING_OUTPUT=//C/MessageOld >> .env
 ```
 Then go ahead and issue this command
```
	$ docker-compose up
```
 This will get you up and running with GTAS in your local env.
Access the application at _**http://localhost:8080/gtas**_

---
**Build Instructions**
 
There are couple of ways to engage Docker within GTAS
1.   _**Option1:**_  Type in individual Docker commands
2.   _**Option2:**_  Let   __**docker-compose**__    handle the process of building and deploying
 

 **Option 1 - Docker commands**
    You would need to build two images for GTAS to run , 
        Image 1: _**gtas_tomcatgtas**_
        Image 2: _**gtas_mariahost**_
        
From under GTAS root folder, issue these commands
        
```sh
        $ docker build -f gtas-parent/docker/tomcat.Dockerfile .
```
This would build you a _**gtas_tomcatgtas**_ image, the following command will let you check whether this image has been created or not.
```sh
        $ docker images
```
This should display something like this
```sh
    λ  docker images
REPOSITORY              TAG                 IMAGE ID            CREATED             SIZE
gtas_tomcatgtas         latest              28c1f48c7838        2 days ago          549MB
```

Repeat similar process to build _**gtas_mariahost**_ image

From under _**gtas-parent/gtas-commons**_ directory
```sh
        $ docker build -f db.Dockerfile .
```
        
This would build you a _**gtas_mariahost**_ image, again check it with
```sh
        $ docker images
```
This should display something like this 

```sh
gtas_mariahost          latest              85a27e79b2bb        2 days ago          339MB
```

Once you are done with these steps, skip to **Run Containers** section

 **Option 2 - docker-compose**

An easier option is to kick-off docker-compose YAML file that will build images and run containers for us.

From under the GTAS root directory, issue this command
```sh
        $ docker-compose build
```
This single command will build you two images, _**gtas_mariahost**_  and _**gtas_tomcatgtas**_.

Now, you can check the images again with 
```sh
        $ docker images
```
and proceed to **Run Containers** section


**Run Containers**

These following commands help you to start Docker containers
- Start with mariahost first
```sh
        $ docker run -d --rm --name mariahost -p 3306:3306 gtas_mariahost
```

- Then kick off Tomcat
```sh
$ docker run -d --rm --name tomcat -p 8080:8080 --link mariahost:mariahost gtas_tomcatgtas
```

Give it a couple mins

and then open up the login screen going to _**http://localhost:8080/gtas**_