#!/bin/sh

# update self
git pull

# build
fab docker_build
