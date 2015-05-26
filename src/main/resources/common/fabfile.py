# -*- coding: utf-8 -*-
__author__ = 'andy'
from datetime import datetime

from fabric.api import *

HUB_NAME = "${group.name}"
APP_NAME = "${name}"
APP_VERSION = "${version}"
APP_PORT = "${server.port}"
FAILED = False


def check_version():
    global FAILED
    old_app_version = local("head -n1 version", True)
    if APP_VERSION <= old_app_version:
        print "Head Version({0} in pom.xml) must bigger than Current Version({1}), EXIT!".format(APP_VERSION,
                                                                                                 old_app_version)
        FAILED = True


def docker_build():
    if FAILED:
        return

    head_version = local("git rev-parse HEAD", True)

    # write info
    local("echo '{0}' > version".format(APP_VERSION))
    local("echo 'Name: {0}/{1}' >> version".format(APP_NAME, HUB_NAME))
    local("echo 'Build: {0}.{1}' >> version".format(APP_VERSION, head_version[:8]))
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


def docker_run(container_id=None):
    if FAILED:
        return

    # docker host
    docker_host = local("ip -4 addr show scope global dev docker0 | grep inet | awk '{print $2}' | cut -d / -f 1", True)

    # kill old container
    running_id = local("docker ps -a | grep {0} | grep 'Up' | cut -d' ' -f 1".format(APP_NAME.lower()), True)
    running_id = running_id.strip()

    if running_id and len(running_id) > 0:
        local("docker stop {0}".format(running_id))
    else:
        local("docker stop `cat container`")

    # run
    if container_id:

        # already run?
        exited_id = local("docker ps -a | grep {0} | grep 'Exited' | cut -d' ' -f 1".format(container_id), True)
        exited_id = exited_id.strip()

        if exited_id and len(exited_id) > 0:
            local("docker start {0}".format(exited_id))

            # keep container_id
            local("echo '{0}' > container".format(container_id[:12]))

        else:
            print "Can't run, container:{0} not found or not exited!".format(container_id)

    else:
        container_id = local(
            "docker run {4} {5} {6} -dp {0}:{0} {1}/{2}:{3}".format(APP_PORT,
                                                                    APP_NAME.lower(),
                                                                    HUB_NAME.lower(),
                                                                    APP_VERSION.lower(),
                                                                    "--add-host=docker_host:" + docker_host,
                                                                    "--ulimit nofile=65535:65535",
                                                                    "-v /data/logs:/data/logs"), True)

        # keep rollback_id
        local("cp container container.rollback")

        # keep container_id
        local("echo '{0}' > container".format(container_id[:12]))


def docker_rollback():
    has_run = local("[ -f 'container' ] && echo 'yes' || echo 'no'", True)
    has_rollback = local("[ -f 'container.rollback' ] && echo 'yes' || echo 'no'", True)

    if has_run == 'yes' and has_rollback == 'yes':
        run_id = local("cat container", True)
        rollback_id = local("cat container.rollback", True)

        local("docker stop {0}".format(run_id))
        local("docker start {0}".format(rollback_id))
        local("rm -f container.rollback")
        local("echo '{0}' > container".format(rollback_id))

    else:
        print "Can't doing rollback, container.rollback file not found!"


def docker_cleanup():
    exited_containers = local("docker ps -a | grep " + APP_NAME.lower() + " | grep 'Exited' | awk '{print $1}'", True)
    exited_containers = exited_containers.split("\n")

    exited_images = local("docker ps -a | grep " + APP_NAME.lower() + " | grep 'Exited' | awk '{print $2}'", True)
    exited_images = exited_images.split("\n")

    if exited_containers and len(exited_containers) > 0:
        for container in exited_containers:
            local("docker rm -v {0}".format(container))

    if exited_images and len(exited_images) > 0:
        for image in exited_images:
            local("docker rmi {0}".format(image))

    local("rm -f container.rollback")

    print "Docker cleanup!"
