host=localhost


newUser=${webappUser}
newDbPassword=${webappPass}
commands="CREATE USER '${newUser}'@'${host}' IDENTIFIED BY '${newDbPassword}';GRANT ALL privileges ON gtas.* TO '${newUser}'@'${host}' IDENTIFIED BY '${newDbPassword}';FLUSH PRIVILEGES;SHOW GRANTS FOR '${newUser}'@'${host}';COMMIT;"

echo "${commands}" | /usr/bin/mysql --user="${existingUser}" --password="${existingUserPassword}"




newUser=${etlUser}
newDbPassword=${etlPass}
commands="CREATE USER '${newUser}'@'${host}' IDENTIFIED BY '${newDbPassword}';GRANT ALL privileges ON gtas.* TO '${newUser}'@'${host}' IDENTIFIED BY '${newDbPassword}';FLUSH PRIVILEGES;SHOW GRANTS FOR '${newUser}'@'${host}';COMMIT;"

echo "${commands}" | /usr/bin/mysql --user="${existingUser}" --password="${existingUserPassword}"




newUser=${processorUser}
newDbPassword=${processorPass}
commands="CREATE USER '${newUser}'@'${host}' IDENTIFIED BY '${newDbPassword}';GRANT ALL privileges ON gtas.* TO '${newUser}'@'${host}' IDENTIFIED BY '${newDbPassword}';FLUSH PRIVILEGES;SHOW GRANTS FOR '${newUser}'@'${host}';COMMIT;"

echo "${commands}" | /usr/bin/mysql --user="${existingUser}" --password="${existingUserPassword}"



