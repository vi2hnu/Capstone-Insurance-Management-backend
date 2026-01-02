FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/Identity-Service-0.0.1-SNAPSHOT.jar /app/Identity-Service-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "Identity-Service-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=docker"]