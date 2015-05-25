FROM errordeveloper/oracle-jre

ADD ${name}/lib/ /${name}/lib/

ADD ${name}/ /${name}/

CMD ["-cp", "/${name}/", "org.springframework.boot.loader.JarLauncher", "--server.port=${server.port}"]

EXPOSE ${server.port}