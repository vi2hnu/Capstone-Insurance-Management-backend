FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/Config-Server-0.0.1-SNAPSHOT.jar /app/Config-Server-0.0.1-SNAPSHOT.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "Config-Server-0.0.1-SNAPSHOT.jar"]