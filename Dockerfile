FROM java

ADD target/scala-2.12/sclmanager-assembly-0.1.0-SNAPSHOT.jar /app.jar

EXPOSE 8082
CMD java -cp /app.jar JettyLauncher
