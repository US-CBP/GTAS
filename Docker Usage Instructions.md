# Docker Instructions
----------
 **Installation**
- Download and Install **Docker Toolbox** for Windows 	
					[Docker Toolbox](https://docs.docker.com/toolbox/toolbox_install_windows/)
- Check whether you have everything up and running properly 

```sh
$ docker version
```


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
        $ docker build -f gtas-parent/docker/tomcat.Dockerfile -t gtas_tomcatgtas .
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
        $ docker build -f db.Dockerfile -t gtas_mariahost .
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














