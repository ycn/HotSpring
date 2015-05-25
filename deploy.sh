#!/bin/sh

# update self
git pull

# build
mvn clean package -Denv=prod

# fetch config
cp target/classes/fabfile.py .

# docker
fab docker_build
