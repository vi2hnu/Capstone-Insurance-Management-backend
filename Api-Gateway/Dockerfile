FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/ApiGateway-0.0.1-SNAPSHOT.jar /app/ApiGateway-0.0.1-SNAPSHOT.jar

EXPOSE 9000

ENTRYPOINT ["java", "-jar", "ApiGateway-0.0.1-SNAPSHOT.jar"]