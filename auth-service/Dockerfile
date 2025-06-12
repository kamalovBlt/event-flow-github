FROM eclipse-temurin:21-jre

WORKDIR /event-flow

COPY auth-impl/build/libs/auth-impl-1.0-SNAPSHOT.jar auth-service.jar

CMD ["java", "-jar", "auth-service.jar"]