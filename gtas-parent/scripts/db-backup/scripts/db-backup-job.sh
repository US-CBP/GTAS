#!/bin/bash


file="./db-backup.properties"

# Function to get dateValue as String

function getDateAsStr()
{
  dateStr=$(date '+%Y%m%d-%H%M%S');
} 




#  Function to update properties file  

 updateBackupCount(){

   if [ $incCount -lt $incLimit ]
      then  newCount=$(($incCount + 1 ));
   else
	newCount="0"
   fi
   
   sed -i -e "/.*inc_count*./ s/.*/inc_count=$newCount/" "$file"
 }


#  Function to update last incremental backup dir value

 updateLastIncDir(){

   sed -i -e "/.*last_inc_dir*./ s/.*/last_inc_dir=$1/" "$file"
 }





#  Function to validate required fields

 validateRequiredFields() {

     	if [ -z "$dbUserName" ]
	  then echo " ### ERROR! The username for the MariaDB Backup was not found on the db_backup.properties file."
	  	hasErrors=true
	fi

	if [ -z "$dbPassword" ]
   	  then echo " ### ERROR! The password for the MariaDB Backup was not found on the db_backup.properties file."
		hasErrors=true;
	fi

	if [ -z "$backupDir" ]
	  then echo " ### ERROR! The value for backup_dir property  was not found on the db_properties file."
		hasErrors=true
	fi

	if [ -z "$baseBackupPrefix" ]
	  then echo " ### ERROR! The value for base_backup_prefix property was not found on the db_backup.properties file." 
		hasErrors=true
	fi

	if [ -z "$incBackupPrefix" ]
	  then echo " ### ERROR! The value for the inc_backup_prefix property was not found on the db_backup.properties file."
	 	hasErrors=true
        fi

        if [ -z "$incLimit" ]
          then echo " ### ERROR! The value for the inc_limit property was not found on the db_backup.properties file."
                hasErrors=true
        fi

        if [ -z "$incCount" ]
          then echo " ### ERROR! The value for the inc_count property was not found on the db_backup.properties file."
                hasErrors=true
        fi

	if [ "$hasErrors" = true ]
	
	 	then 
			echo "STEP 2 OF 6 FAILED - ERROR!  Either required properties are not listed on db-backup.properties or has null values.See errors (above)."
	 		exit 1
		
		else
			echo "STEP 2 OF 6 COMPLETED SUCCESSFULLY - verified propoerties listed on db-backup.prperties."
	fi





 }




#  Function to read properties file 
 
 readPropertiesFile(){

	if [ -f "$file" ]
 	then
   		echo "### => $file was found!"

		while IFS='=' read -r key value
  		do
			if [ "$key"  = "userName" ]
				then dbUserName=$value
			elif [ "$key" = "password" ]
				then dbPassword=$value
			elif [ "$key" = "backup_dir" ]
				then backupDir=$value
			elif [ "$key" = "base_backup_prefix" ] 
				then baseBackupPrefix=$value
			elif [ "$key" = "inc_backup_prefix" ]
				then incBackupPrefix=$value 
			elif [ "$key" = "inc_limit" ]
				then incLimit=$value
			elif [ "$key" = "inc_count" ]
				then incCount=$value
			elif [ "$key" = "last_inc_dir" ]
				then lastIncDir=$value	
 			fi
	
	 	done < "$file"	
 		echo "STEP 1 OF 6 COMPLETED SUCCESSFULLY - created the backup directory $backupPath."
	
	else
		echo "STEP 1 OF 6 FAILED - ERROR! $file not found!"
		exit 1
 	fi


  }


 
# ##########  The backup process starts here  ###############

	readPropertiesFile
 	validateRequiredFields

	backupPath=""
	if [ "$incCount" -eq 0 ]
	then
		echo " "
		echo "Running the full backup job ...."	
		getDateAsStr
		baseBackupDir="${baseBackupPrefix}-${dateStr}"		
		backupPath="${backupDir}/${baseBackupDir}" 
		newBaseDir="${baseBackupDir}"
	
		mkdir -p -- "$backupPath"
		if [ $? -eq 0 ]
		 then
 		   	echo "STEP 3 OF 6 COMPLETED SUCCESSFULLY - created backup directory: $backupPath."
		else
    			echo "STEP 3 OF 6 FAILED - ERROR! Could not create backup directory: $backupPath."
			exit 1
		fi

	 	mariabackup --backup --target-dir="$backupPath" --user="$dbUserName" --password="$dbPassword"
		if [ $? -eq 0 ] 
		then
                        echo "STEP 4 OF 6 COMPLETED SUCCESSFULLY - created full backu-up successfully in $backupPath"
                else
                        echo "STEP 4 OF 6 FAILED - ERROR! An error has occurred when creating a full backup."
			echo "Removing directories created by this process because the data could be corrupted....."
                        rm -r $backupPath
			exit 1
                fi
	else
		echo " "	
		echo "Running the incremental backup job ...."
		getDateAsStr
		incBackupDir="${incBackupPrefix}-${dateStr}"
		backupPath="${backupDir}/${incBackupPrefix}-${dateStr}"
		incBaseDir="${backupDir}/${lastIncDir}"
		newBaseDir="${incBackupDir}"

		if [ -d "$incBaseDir" ]
       		then
	
			mkdir -p -- "$backupPath"
			if [ $? -eq 0 ]
		 		then
                        	echo "STEP 3 OF 6 COMPLETED SUCCESSFULLY - created backup directory: $backupPath."
                	else
                        	echo "STEP 3 OF 6 FAILED - ERROR! Could not create backup directory: $backupPath."
				exit 1
                	fi
		

			mariabackup --backup  --target-dir="$backupPath" --incremental-basedir="$incBaseDir" --user="$dbUserName" --password="$dbPassword"
			if [ $? -eq 0 ] 
			then
                        	echo "STEP 4 OF 6 COMPLETED SUCCESSFULLY - created incremental backu-up successfully in $backupPath"
                	else
                        	echo "STEP 4 OF 6 FAILED - ERROR! An error has occurred when creating a full backup."
				echo "Removing directories created by this process because the data could be corrupted....."
				rm -r $backupPath
				exit 1
                	fi

	 	else

			echo "## ERROR! The required Base Directory, $incBaseDir, for the Incremental Backup was not found!."
			echo "This is might be caused by a failure on the last incremental backup attempt "
			echo "or the last_inc_dir value was manually altered after the last incremental backup process run." 
			exit 1 
		fi

	fi

		
	
                updateLastIncDir "$newBaseDir"
                if [ $? -eq 0 ] 
		then
                        echo "STEP 5 OF 6 COMPLETED SUCCESSFULLY - updated the last_inc_dir property to $newBaseDir on db-backup.properties file."
                else
                        echo "STEP 5 OF 6 FAILED - ERROR! An error has occurred when updating last_inc_dir property on db-backup.properties file."
			echo "Removing directories created by this process to avoid data integrity issues....."
                        rm -r $backupPath

			exit 1
                fi

		sudo chown -R mysql $backupPath

                updateBackupCount
                if [ $? -eq 0 ] 
		then
                        echo "STEP 6 OF 6 COMPLETED SUCCESSFULLY - updated the inc_count property to $newCount on db-backup.properties file."
                else
                        echo "STEP 6 OF 6 FAILED - ERROR! An error has occurred when updating inc_count property on db-backup.properties file."
			echo "Removing directories created by this process to avoid data integrity issues....."
                        rm -r $backupPath

			exit 1
                fi



	echo "------------ THE DATABASE BACKUP PROCESS IS COMPLETED -------------------"
	exit 0

