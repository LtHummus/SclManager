# STAGE 1: build JAR file

FROM hseeberger/scala-sbt

COPY project/ project/
COPY src/ src/

RUN sbt assembly

#==========================

# STAGE 2: build docker image to run server

FROM java

COPY --from=0 /root/target/scala-2.12/sclmanager-assembly-0.1.0-SNAPSHOT.jar /app.jar

EXPOSE 8082
CMD java -cp /app.jar JettyLauncher
