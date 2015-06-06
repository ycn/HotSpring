# FROM errordeveloper/oracle-jre
FROM java:8-jre
MAINTAINER Andy Yuan <andy@ycn.cc>

ADD ${name}/lib/ /${name}/lib/
ADD ${name}/ /${name}/
CMD ["java", "-cp", "/${name}/", "org.springframework.boot.loader.JarLauncher", "--server.port=${server.port}"]

EXPOSE ${server.port}