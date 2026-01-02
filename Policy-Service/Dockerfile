FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/Policy-Service-0.0.1-SNAPSHOT.jar /app/Policy-Service-0.0.1-SNAPSHOT.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "Policy-Service-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=docker"]