FROM errordeveloper/oracle-jre

ADD ${name}/lib/ /${name}/lib/

ADD ${name}/ /${name}/

CMD ["java", "-cp", "/${name}/", "org.springframework.boot.loader.JarLauncher", "--server.port=${server.port}"]

EXPOSE ${server.port}