SET MAVEN_OPTS='-Xmx2048m -Xms2048m -Xmn1024m -Xss512k -XX:PermSize=256m -XX:MaxPermSize=256m -XX:+CMSClassUnloadingEnabled -XX:+CMSClassUnloadingEnabled -XX:+UseConcMarkSweepGC -Xdebug -Xrunjdwp:transport=dt_socket,address=1317,suspend=n,server=y'

mvn clean package -Denv=dev

mvn spring-boot:run
