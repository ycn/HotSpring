# -*- coding: utf-8 -*-
__author__ = 'andy'
from fabric.api import *

APP_NAME = "HotSpring"
APP_VERSION = "1.0"


def docker_build():

    # package
    local("mvn clean package -Denv=prod")

    with lcd("target/"):
        # explode fat jar
        local("rm -rf {0}-{1}/ && unzip -q {0}-{1}.jar -d {0}".format(APP_NAME, APP_VERSION))
        # revert the timestamp changes
        local("find ./{0}/lib/ -exec touch -c -m -t 201407070000 {} \;".format(APP_NAME))
        # cp DockerFile
        local("cp ../DockerFile .")
        # build
        local("docker build .")