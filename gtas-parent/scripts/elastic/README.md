## ELK stack installation (TLS enabled)
### Requirements
    sudo yum install git, wget, maven, unzip -y
### Configuration

  Configuration files can be found at `GTAS/gtas-parent/scripts/elastic/config/*`. 

### Installation 

`` Follow the steps below to set up ELK stack on a single server ``

1. Linux

    *  Clone GTAS from github (https://github.com/US-CBP/GTAS.git)

        ```bash
        git clone --single-branch --branch dev https://github.com/US-CBP/GTAS.git
        ```
    * Browse to GTAS
        ```bash
        cd GTAS/gtas-parent/scripts/elastic/install/linux/
        ```
    * Install all ELK stack (Installs elasticsearch, kibana, logstash and configures self-signed certificates)

        ```bash
         ./install_all.sh
        ```

    * Manage each ELK stack separately 

        > Each script below uses yum to (install/remove) and systemd service to (start/stop).

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

        ** Create SSL certificates and enable TLS for the ELK stack **
        
        ```
        ./setup-security.sh
        ```

        `` Note: ``

        1. Prints randomly generated password on the logs
        
        2. Answer "y" to the following question: 
            `the Failure to do so will result in reduced security. Continue without password protection on the keystore? [y/N]`
