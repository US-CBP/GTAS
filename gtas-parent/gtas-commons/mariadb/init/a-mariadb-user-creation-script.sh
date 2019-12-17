
NEW_USER=$(cat /run/secrets/mysql_webapp_user)
NEW_DB_PASSWORD=$(cat /run/secrets/mysql_webapp_password)
commands="CREATE USER '${NEW_USER}'@'%' IDENTIFIED BY '${NEW_DB_PASSWORD}';GRANT ALL privileges ON *.* TO '${NEW_USER}'@'%' IDENTIFIED BY '${NEW_DB_PASSWORD}';FLUSH PRIVILEGES;"

echo "${commands}" | /usr/bin/mysql --user="root" --password=${MYSQL_ROOT_PASSWORD}


NEW_USER=$(cat /run/secrets/mysql_etl_user)
NEW_DB_PASSWORD=$(cat /run/secrets/mysql_etl_password)
commands="CREATE USER '${NEW_USER}'@'%' IDENTIFIED BY '${NEW_DB_PASSWORD}';GRANT ALL privileges ON *.* TO '${NEW_USER}'@'%' IDENTIFIED BY '${NEW_DB_PASSWORD}';FLUSH PRIVILEGES;"

echo "${commands}" | /usr/bin/mysql --user="root" --password=${MYSQL_ROOT_PASSWORD}


NEW_USER=$(cat /run/secrets/mysql_processor_user)
NEW_DB_PASSWORD=$(cat /run/secrets/mysql_processor_password)
commands="CREATE USER '${NEW_USER}'@'%' IDENTIFIED BY '${NEW_DB_PASSWORD}';GRANT ALL privileges ON *.* TO '${NEW_USER}'@'%' IDENTIFIED BY '${NEW_DB_PASSWORD}';FLUSH PRIVILEGES;"

echo "${commands}" | /usr/bin/mysql --user="root" --password=${MYSQL_ROOT_PASSWORD}


NEW_USER=$(cat /run/secrets/mysql_healthcheck_user)
NEW_DB_PASSWORD=$(cat /run/secrets/mysql_healthcheck_password)
commands="CREATE USER '${NEW_USER}'@'%' IDENTIFIED BY '${NEW_DB_PASSWORD}';GRANT SHOW DATABASES ON *.* TO '${NEW_USER}'@'%' IDENTIFIED BY '${NEW_DB_PASSWORD}';FLUSH PRIVILEGES;"

echo "${commands}" | /usr/bin/mysql --user="root" --password=${MYSQL_ROOT_PASSWORD}


NEW_USER=""
NEW_DB_PASSWORD=""
