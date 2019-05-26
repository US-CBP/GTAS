## ELK stack installation
### Requirements
    sudo yum install git, wget, maven -y
### Configuration

  Configuration files can be found at `GTAS/gtas-parent/scripts/elastic/config/*`. 

### Installation

1. Linux

    *  Clone GTAS from github (https://github.com/US-CBP/GTAS.git)

        ```bash
        git clone --single-branch --branch dev https://github.com/US-CBP/GTAS.git
        ```
    * Browse to GTAS
        ```bash
        cd GTAS/gtas-parent/scripts/elastic/install/linux/
        ```
    * Manage all ELK stack

        **install**: installs elastic search, kibana and logstash as a serivce. The services will start automatically on boot.

        **start**: starts all ELK services.

        **stop**: stops all ELK services.

        **uninstall**: stops the serivces and remove the packges from the system.

        ```bash
         ./(install/start/stop/uninstall)_all.sh 
        ```

    * Manage each ELK stack separately 

        > Each script below uses systemd service to (start/stop) and yum to (install/uninstall). The scripts are also used to bootstrap Centos7 VM.

        Elastic Search
        ```bash
        ./elasticsearch/(install/start/stop/uninstall).sh 
        ``` 
        Kibana
        ```bash
        ./kibana/(install/start/stop/uninstall).sh
        ```

        Logstash
        
        **install**: installs logstash as a serivce. Copies all  logstash config files, elastic search templates, sql scripts and java client jar for mariadb into logstash working directory. The service will start automatically on boot.

         ```bash
        ./logstash/(install/start/stop/uninstall).sh
        ```