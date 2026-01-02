FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/Service-Registry-0.0.1-SNAPSHOT.jar /app/Service-Registry-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/Service-Registry-0.0.1-SNAPSHOT.jar"]