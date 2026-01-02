FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/Billing-Service-0.0.1-SNAPSHOT.jar /app/Billing-Service-0.0.1-SNAPSHOT.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "Billing-Service-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=docker"]