FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/Provider-Service-0.0.1-SNAPSHOT.jar /app/Provider-Service-0.0.1-SNAPSHOT.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "Provider-Service-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=docker"]