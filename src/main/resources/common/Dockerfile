# use minimal 8-jre
FROM errordeveloper/oracle-jre
MAINTAINER Andy Yuan <andy@ycn.cc>

ADD ${name}/lib/ /${name}/lib/
ADD ${name}/ /${name}/
CMD ["-cp", "/${name}/", "org.springframework.boot.loader.JarLauncher", "--server.port=${server.port}"]

EXPOSE ${server.port}