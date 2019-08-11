#!/bin/sh
# Deletes old war file
#
rm /opt/apache-tomcat-8.5.34/webapps/gtas.war
if [ $? -eq 0 ]
then
  echo "Success: The gtas.war file was removed successfully."
  exit 0
else
  echo "Warning!: The gtas.war file  was not found or could not be deleted." 
  exit 0
fi
rm -r /opt/apache-tomcat-8.5.34/webapps/gtas
if [ $? -eq 0 ]
then
  echo "Success: The gtas expanded war folder was removed successfully."
  exit 0
else
  echo "Warning!: The gtas expanded war folder could not be found or could not be removed!" 
  exit 0
fi
