#!/bin/bash

rm -rf stack-docker

git clone --depth=2 --branch=7.0fixes https://github.com/elastic/stack-docker.git 
rm -rf ./stack-docker/.git

cd stack-docker

# docker-compose -f setup/setup.yml run --rm setup