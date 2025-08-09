FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/healthcare-qr-sharing-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]