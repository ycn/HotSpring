# -*- coding: utf-8 -*-
__author__ = 'andy'
from datetime import datetime

from fabric.api import *

HUB_NAME = "${group.name}"
APP_NAME = "${name}"
APP_VERSION = "${version}"
APP_PORT = "${server.port}"


def docker_build():
    head_version = local("git rev-parse HEAD", True)

    # write info
    local("echo 'Name: {0}/{1}' > version".format(APP_NAME, HUB_NAME))
    local("echo 'Version: {0}' >> version".format(APP_VERSION))
    local("echo 'Build: {0}' >> version".format(head_version))
    local("echo 'Date: {0}' >> version".format(datetime.today()))
    local("git log -1 >> version")

    with lcd("target/"):
        # explode fat jar
        local("rm -rf {0}-{1}/ && unzip -q {0}-{1}.jar -d {0}".format(APP_NAME, APP_VERSION))
        # revert the timestamp changes
        local("find ./{0}/lib/ -exec touch -c -m -t 201407070000.00".format(APP_NAME) + " {} \;")
        # cp DockerFile
        local("cp ./classes/Dockerfile .")
        # cp version
        local("cp ../version ./{0}/".format(APP_NAME))
        # build
        local("docker build -t {0}/{1} .".format(APP_NAME.lower(),
                                                 HUB_NAME.lower()))

        local("docker tag {0}/{1}:latest {0}/{1}:{2}".format(APP_NAME.lower(),
                                                             HUB_NAME.lower(),
                                                             APP_VERSION.lower()))


def docker_run():
    # kill old container
    local("docker stop `cat container`")

    # run
    container_id = local("docker run -dp {0}:{0} {1}/{2}:{3}".format(APP_PORT,
                                                                     APP_NAME.lower(),
                                                                     HUB_NAME.lower(),
                                                                     APP_VERSION.lower()), True)
    # keep container_id
    local("echo '{0}' > container".format(container_id[:12]))