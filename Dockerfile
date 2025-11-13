FROM eclipse-temurin:21-jre-jammy
LABEL authors="Yauheni Sviarchkou"

WORKDIR /app
COPY build/libs/user-service-0.0.1-SNAPSHOT.jar user-service.jar

ENTRYPOINT ["java", "-jar", "user-service.jar", "--spring.profiles.active=docker", "--server.address=0.0.0.0"]

