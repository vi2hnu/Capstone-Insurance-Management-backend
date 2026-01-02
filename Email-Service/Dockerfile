FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/EmailService-0.0.1-SNAPSHOT.jar /app/EmailService-0.0.1-SNAPSHOT.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "EmailService-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=docker"]