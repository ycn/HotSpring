# -*- coding: utf-8 -*-
__author__ = 'andy'
from datetime import datetime

from fabric.api import *

HUB_NAME = "hotdev"
APP_NAME = "HotSpring"
APP_VERSION = "1.0"


def docker_build():
    head_version = local("git rev-parse HEAD", True)

    # write info
    local("echo 'Build: {0}' > version".format(head_version))
    local("echo 'Date: {0}' >> version".format(datetime.today()))
    local("git log -1 >> version")

    # package
    local("mvn clean package -Denv=prod")

    with lcd("target/"):
        # explode fat jar
        local("rm -rf {0}-{1}/ && unzip -q {0}-{1}.jar -d {0}".format(APP_NAME, APP_VERSION))
        # revert the timestamp changes
        local("find ./{0}/lib/ -exec touch -c -m -t 201407070000.00".format(APP_NAME) + " {} \;")
        # cp DockerFile
        local("cp ../Dockerfile .")
        # build
        local("docker build -t {0}/{1} .".format(APP_NAME.lower(), HUB_NAME.lower()))
        local("docker tag {0}/{1}:latest {0}/{1}:{2}".format(APP_NAME.lower(), HUB_NAME.lower(), APP_VERSION.lower()))