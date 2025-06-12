FROM eclipse-temurin:21-jre

WORKDIR /event-flow

COPY event-impl/build/libs/event-impl-1.0-SNAPSHOT.jar event-service.jar

CMD ["java", "-jar", "event-service.jar"]