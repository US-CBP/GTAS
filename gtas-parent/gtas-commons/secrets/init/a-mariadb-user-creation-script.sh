
NEW_USER=$(cat /run/secrets/mysql-webapp-user)
NEW_DB_PASSWORD=$(cat /run/secrets/mysql-webapp-password)
commands="CREATE USER '${NEW_USER}'@'%' IDENTIFIED BY '${NEW_DB_PASSWORD}';GRANT ALL privileges ON *.* TO '${NEW_USER}'@'%' IDENTIFIED BY '${NEW_DB_PASSWORD}';FLUSH PRIVILEGES;"

echo "${commands}" | /usr/bin/mysql --user="root" --password=${MYSQL_ROOT_PASSWORD}


NEW_USER=$(cat /run/secrets/mysql-etl-user)
NEW_DB_PASSWORD=$(cat /run/secrets/mysql-etl-password)
commands="CREATE USER '${NEW_USER}'@'%' IDENTIFIED BY '${NEW_DB_PASSWORD}';GRANT ALL privileges ON *.* TO '${NEW_USER}'@'%' IDENTIFIED BY '${NEW_DB_PASSWORD}';FLUSH PRIVILEGES;"

echo "${commands}" | /usr/bin/mysql --user="root" --password=${MYSQL_ROOT_PASSWORD}


NEW_USER=$(cat /run/secrets/mysql-processor-user)
NEW_DB_PASSWORD=$(cat /run/secrets/mysql-processor-password)
commands="CREATE USER '${NEW_USER}'@'%' IDENTIFIED BY '${NEW_DB_PASSWORD}';GRANT ALL privileges ON *.* TO '${NEW_USER}'@'%' IDENTIFIED BY '${NEW_DB_PASSWORD}';FLUSH PRIVILEGES;"

echo "${commands}" | /usr/bin/mysql --user="root" --password=${MYSQL_ROOT_PASSWORD}


NEW_USER=$(cat /run/secrets/mysql-healthcheck-user)
NEW_DB_PASSWORD=$(cat /run/secrets/mysql-healthcheck-password)
commands="CREATE USER '${NEW_USER}'@'%' IDENTIFIED BY '${NEW_DB_PASSWORD}';GRANT SHOW DATABASES ON *.* TO '${NEW_USER}'@'%' IDENTIFIED BY '${NEW_DB_PASSWORD}';FLUSH PRIVILEGES;"

echo "${commands}" | /usr/bin/mysql --user="root" --password=${MYSQL_ROOT_PASSWORD}

NEW_USER=$(cat /run/secrets/mysql-logstash-user)
NEW_DB_PASSWORD=$(cat /run/secrets/mysql-logstash-password)
commands="CREATE USER '${NEW_USER}'@'%' IDENTIFIED BY '${NEW_DB_PASSWORD}';GRANT ALL privileges ON *.* TO '${NEW_USER}'@'%' IDENTIFIED BY '${NEW_DB_PASSWORD}';FLUSH PRIVILEGES;"

echo "${commands}" | /usr/bin/mysql --user="root" --password=${MYSQL_ROOT_PASSWORD}


NEW_USER=""
NEW_DB_PASSWORD=""