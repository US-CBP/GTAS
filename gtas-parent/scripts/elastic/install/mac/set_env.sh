#!/bin/bash


# Set environment variable for elastic installation location and version

# set below the location where you want to have elastic stack installed
export ES_INSTALL_LOCATION=~/US-CBP/dev/elastic_dev_install

export ES_INSTALL_VERSION="7.2.0"

# create the folder if it does not exist

mkdir -p $ES_INSTALL_LOCATION
