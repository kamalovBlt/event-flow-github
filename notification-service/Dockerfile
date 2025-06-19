FROM eclipse-temurin:21-jre

WORKDIR /event-flow

COPY notification-impl/build/libs/notification-impl-1.0-SNAPSHOT.jar notification-service.jar

CMD ["java", "-jar", "notification-service.jar"]