FROM openjdk:11-jre

RUN mkdir /app
WORKDIR /app

ENTRYPOINT ["java","-jar","app.jar"]

COPY target/wss-demo*-with-dependencies.jar app.jar
