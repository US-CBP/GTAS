#!/bin/bash

CURRENT_DIR=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd $CURRENT_DIR

VDI_FILE_NAME=CentOS-7-x86_64-GenericCloud.SCRATCH.vdi

VB_VMS_LOCATION="$(cd ~/VirtualBox VMs; pwd -P)"
VDI_NAME="$VB_VMS_LOCATION"/CentOS-7-x86_64-GenericCloud-1901.vdi
DOWNLOAD_TAR_NAME="CentOS-7-x86_64-GenericCloud.raw.tar.gz"
RAW_FILE_NAME="CentOS-7-x86_64-GenericCloud-1901.raw"

VM_NAME="GTASCentos7"

echo "Script location: " $SCRIPT_LOCATION

echo "VirtualBox VMs location: " $VB_VMS_LOCATION

echo "VDI Name: " $VB_VMS_LOCATION

##
## Download Centos cloud image
##

download_tar_iso(){
    if [ ! -f "$DOWNLOAD_TAR_NAME" ]
    then
    	wget https://cloud.centos.org/centos/7/images/$DOWNLOAD_TAR_NAME
    fi
}

##
## Extract the compressed file. 
## 
## The extracted file will be a single raw file
##
extract_tar(){
	if [ -f "$DOWNLOAD_TAR_NAME" ]
	then
    	tar xvf $DOWNLOAD_TAR_NAME
    fi
}

##
## Convert the raw image into VDI image using the VBoxManage tool
## 

convert_raw_to_vdi(){
    if [ -f "$RAW_FILE_NAME" ]
	then
    VBoxManage convertfromraw $RAW_FILE_NAME --format vdi "$VDI_NAME"
    fi
}

##
## Generate a new UUID for the scratch image. This is necessary anytime a disk image is duplicated
## 
gen_uuid(){
 	if [ -f "$VDI_NAME" ]
	then
    VBoxManage internalcommands sethduuid "$VDI_NAME"
    fi
}	

import_vdi(){
    # Create a new VM
    VBoxManage createvm --name $VM_NAME --ostype RedHat_64 --register

    ## Set the cpu and memory for vm
    VBoxManage modifyvm $VM_NAME --cpus 2 --memory 4048 --vram 12
    
    # Attach a virtual media
    VBoxManage storagectl $VM_NAME --name "SATA Controller" --add sata --bootable on

    # Attach a
    VBoxManage storagectl $VM_NAME --name "IDE Controller" --add ide --bootable on

    ## Attach the hardisk to the SATA controller
    VBoxManage storageattach $VM_NAME --storagectl "SATA Controller" --port 0 --device 0 --type hdd --medium "$VDI_NAME"

    ## Attach seed iso to the IDE controller
    VBoxManage storageattach $VM_NAME --storagectl "IDE Controller" --port 0 --device 0 --type dvddrive --medium ../shared/seed.iso

}

clean(){
	rm -f "$VDI_NAME"
	rm -f $RAW_FILE_NAME
	rm -f $DOWNLOAD_TAR_NAME
}

setup_port_forwarding(){
	VBoxManage modifyvm $VM_NAME --natpf1 "guestssh,tcp,,2222,,22"
	VBoxManage modifyvm $VM_NAME --natpf1 "webapp,tcp,,8081,,8080"
	VBoxManage modifyvm $VM_NAME --natpf1 "kibana,tcp,,5602,,5601"
}

start_vm(){
	if [ -f "$VDI_NAME" ]
	then
    	VBoxManage startvm $VM_NAME
    fi
}

######### #####


## 
download_tar_iso

##
extract_tar

##
convert_raw_to_vdi

## 
gen_uuid

## Import VDI to VrtualBox
import_vdi

## Setup port forwarding
setup_port_forwarding

# Start VM
start_vm

echo "Done"