# -*- coding: utf-8 -*-
__author__ = 'andy'
from datetime import datetime
import sys
import time

from fabric.api import *


HUB_NAME = "${group.name}"
APP_NAME = "${name}"
APP_VERSION = "${version}"
APP_PORT = "${server.port}"
HUB_HOST = "127.0.0.1:5000"

DYUPS_CONF = "/data/dyups/upstream.conf"
DYUPS_HOST = "wx.hotdev.cn"
DYUPS_INTERFACE = "http://127.0.0.1:9999"

STARTUP_DELAY = 2
FAILED = False
DOCKER_HOST = None


def check_version():
    global FAILED
    old_app_version = local("head -n1 version", True)
    if APP_VERSION <= old_app_version:
        print "Head Version({0} in pom.xml) must bigger than Current Version({1}), EXIT!".format(APP_VERSION,
                                                                                                 old_app_version)
        FAILED = True
        sys.exit(1)


def docker_build():
    global DOCKER_HOST

    if FAILED:
        sys.exit(1)

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
        local("docker build -t {0} .".format(APP_NAME.lower()))

        local("docker tag {0}:latest {0}:{1}".format(APP_NAME.lower(),
                                                     APP_VERSION.lower()))

    DOCKER_HOST = get_docker_host()


# 发布latest版本到线上
def docker_publish():
    # latest image
    latest_id = get_latest_image_id()
    if not latest_id:
        print "latest image not found! exit!"
        sys.exit(5)

    local("docker tag {0} {1}/{2}".format(latest_id, HUB_HOST, APP_NAME.lower()))
    local("docker push {0}/{1}".format(HUB_HOST, APP_NAME.lower()))


def docker_run(container_id=None):
    if FAILED:
        sys.exit(1)

    if container_id and not is_running(container_id):  # 运行历史实例

        old_container_id = get_running()

        # start first
        if not start_container(container_id):
            print "Can't start container: " + container_id
            sys.exit(2)

        # delay for app startup
        time.sleep(STARTUP_DELAY)

        # update dyups (change nginx without reload)
        dyups_update(container_id)

        # stop then
        if old_container_id != container_id:
            stop_running(old_container_id)

        # keep rollback info
        if old_container_id:
            local("echo '{0}' > container.rollback".format(old_container_id[:12]))
        local("echo '{0}' > container".format(container_id[:12]))

    else:  # 运行新实例

        old_container_id = get_running()

        # 绑定随机端口
        run_cmd = "docker run {3} {4} {5} {6} {7} -d -p 127.0.0.1::{0} {1}:{2}"
        container_id = local(run_cmd.format(APP_PORT,
                                            APP_NAME.lower(),
                                            APP_VERSION.lower(),
                                            "--add-host=docker_host:" + DOCKER_HOST,
                                            "--ulimit nofile=65535:65535",
                                            "-v /data/logs:/data/logs",
                                            "-v /etc/localtime:/etc/localtime:ro",
                                            '-e "TZ=Asia/Shanghai"'), True)

        # delay for app startup
        time.sleep(STARTUP_DELAY)

        # update dyups (change nginx without reload)
        dyups_update(container_id)

        # stop then
        if old_container_id != container_id:
            stop_running(old_container_id)

        # keep rollback info
        if old_container_id:
            local("echo '{0}' > container.rollback".format(old_container_id[:12]))
        local("echo '{0}' > container".format(container_id[:12]))


def docker_rollback():
    has_rollback_id = local("[ -f 'container.rollback' ] && echo 'yes' || echo 'no'", True)

    if has_rollback_id == 'yes':
        rollback_id = local("cat container.rollback", True)

        old_container_id = get_running()

        # start first
        if not start_container(rollback_id):
            print "Can't start rollback container: " + rollback_id
            sys.exit(2)

        # delay for app startup
        time.sleep(STARTUP_DELAY)

        # update dyups (change nginx without reload)
        dyups_update(rollback_id)

        # stop then
        if old_container_id != rollback_id:
            stop_running(old_container_id)

        # clean rollback info
        local("echo '{0}' > container".format(rollback_id[:12]))
        local("rm -f container.rollback")

    else:
        print "Can't doing rollback, container.rollback file not found!"
        sys.exit(3)


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


def is_running(container_id=None):
    if not container_id:
        container_id = get_running()

    # 判断指定container是否在运行态
    if container_id:
        up_id = local("docker ps -a | grep {0} | grep 'Up' | cut -d' ' -f 1 | head -n 1".format(container_id[:12]), True)
        up_id = up_id.strip()

        if up_id and len(up_id) > 0:
            return True

    return False


def get_docker_host():
    # docker host
    return local("ip -4 addr show scope global dev docker0 | grep inet | awk '{print $2}' | cut -d / -f 1", True)


def get_port(container_id=None):
    if not container_id:
        container_id = get_running()

    if container_id and is_running(container_id):
        return local("docker port {0} {1}".format(container_id[:12], APP_PORT), True)

    return None


def get_running():
    running_id = local("docker ps -a | grep {0} | grep 'Up' | cut -d' ' -f 1 | head -n 1".format(APP_NAME.lower()), True)
    running_id = running_id.strip()
    return running_id


def get_latest_image_id():
    latest_id = local("docker images | grep " + APP_NAME.lower() + " | grep -v '5000' | grep 'latest' | awk -F' ' '{print $3}' | head -n 1", True)
    latest_id = latest_id.strip()
    return latest_id


def stop_running(container_id):
    if container_id and is_running(container_id):
        local("docker stop {0}".format(container_id[:12]))
        return True
    return False


def start_container(container_id):
    if container_id and not is_running(container_id):
        local("docker start {0}".format(container_id[:12]))
        return True
    return False


# 如果不存在会创建, 存在会覆盖
def dyups_update(container_id=None):
    if not container_id:
        container_id = get_running()

    if container_id and is_running(container_id):

        container_port = get_port(container_id)

        if container_port:
            # update dyups upstream.conf case of nginx reload
            upstream = "upstream " + DYUPS_HOST + "{\nkeepalive 100;\nserver " + container_port + ";\n}\n"

            try:
                f = open(DYUPS_CONF, "w")
                f.write(upstream)
            except all:
                print "Unexpected error:", sys.exc_info()[0]
                sys.exit(4)
            else:
                f.close()

            # TODO sync DYUPS_CONF to nginx server

            # change upstream
            local("curl -d 'keepalive 100;server {0};' {1}/upstream/{2}".format(container_port,
                                                                                DYUPS_INTERFACE,
                                                                                DYUPS_HOST))
