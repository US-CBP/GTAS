#!/bin/sh
# Deletes old war file
#
rm /opt/apache-tomcat-8.5.34/webapps/gtas-job-scheduler.war
if [ $? -eq 0 ]
then
  echo "Success: The gtas-job-scheduler.war file was removed successfully."
  exit 0
else
  echo "Warning!: The gtas-job-scheduler.war file  was not found or could not be deleted." 
  exit 0
fi
rm -r /opt/apache-tomcat-8.5.34/webapps/gtas-job-scheduler
if [ $? -eq 0 ]
then
  echo "Success: The gtas-job-scheduler expanded war folder was removed successfully."
  exit 0
else
  echo "Warning!: The gtas-job-scheduler expanded war folder could not be found or could not be removed!" 
  exit 0
fi
