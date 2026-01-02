FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/Claims-Service-0.0.1-SNAPSHOT.jar /app/Claims-Service-0.0.1-SNAPSHOT.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "Claims-Service-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=docker"]