# FROM gcr.io/distroless/java21
FROM eclipse-temurin:23-jre-alpine
RUN apk add --no-cache curl

WORKDIR /app

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENV TZ="Europe/Oslo"
ENTRYPOINT ["java","-jar","app.jar"]
