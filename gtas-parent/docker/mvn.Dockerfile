    FROM sanandreas/mavenjava8gitimage

    ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64
    ENV PATH ${PATH}:/usr/lib/jvm/java-8-openjdk-amd64/bin

    RUN rm -rf GTAS && git clone git://github.com/US-CBP/GTAS.git
    RUN sed -i 's/localhost/'"mariahost"'/' GTAS/gtas-parent/gtas-commons/src/main/resources/hibernate.properties
#    RUN sed -i 's/C:\\MESSAGE/'"/usr/share/gtas/in"'/' GTAS/gtas-parent/gtas-commons/src/main/resources/sql/gtas_data.sql
#    RUN sed -i 's%message.dir.origin=.*$%message.dir.origin=/usr/share/gtas/in%' GTAS/gtas-parent/gtas-job-scheduler-war/src/main/resources/jobScheduler.properties
#    RUN sed -i 's%message.dir.processed=.*$%message.dir.processed=/usr/share/gtas/out%' GTAS/gtas-parent/gtas-job-scheduler-war/src/main/resources/jobScheduler.properties
    RUN cd GTAS/gtas-parent && mvn clean install -D skip.unit.tests=true\
        && cd gtas-commons && mvn hibernate4:export
    RUN cd GTAS/gtas-parent/gtas-commons/target  && cp schema.sql /projects/GTAS/gtas-parent/gtas-commons/src/main/resources/sql/schema.sql
    RUN cd GTAS/gtas-parent/gtas-commons/src/main/resources/sql && echo "DROP DATABASE IF EXISTS gtas;\
    CREATE DATABASE gtas;\
    USE gtas;" > init_sql.sql
    RUN cd GTAS/gtas-parent/gtas-commons/src/main/resources/sql && cat init_sql.sql schema.sql countries.sql carriers.sql airports.sql gtas_data.sql views.sql > init_db.sql\
        && cp init_db.sql /projects/init_db.sql
    RUN cd GTAS/gtas-parent\
        && cp gtas-webapp/target/gtas.war /projects/\
        && cp gtas-job-scheduler-war/target/gtas-job-scheduler.war /projects

    CMD ["/bin/bash"]
