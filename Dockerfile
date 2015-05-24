FROM java:8-jre

ADD HotSrping/lib/ /HotSrping/lib/

ADD HotSrping/ /HotSrping/

CMD ["java", "-cp", "/HotSrping/", "org.springframework.boot.loader.JarLauncher", "--server.port=9080"]

EXPOSE 9080