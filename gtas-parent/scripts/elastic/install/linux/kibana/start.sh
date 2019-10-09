 #export SERVER_HOST=localhost1
 #export KIBANA_PATH_CONFIG=/etc/kibana

echo SERVER_HOST: ${SERVER_HOST}
echo KIBANA_PATH_CONFIG: ${KIBANA_PATH_CONFIG}

systemctl start kibana
