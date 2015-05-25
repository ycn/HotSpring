FROM java:8-jre

ADD HotSpring/lib/ /HotSpring/lib/

ADD HotSpring/ /HotSpring/

CMD ["java", "-cp", "/HotSpring/", "org.springframework.boot.loader.JarLauncher", "--server.port=9080"]

EXPOSE 9080