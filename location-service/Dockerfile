FROM eclipse-temurin:21-jre

WORKDIR /event-flow

COPY location-impl/build/libs/location-impl-1.0-SNAPSHOT.jar location-service.jar

CMD ["java", "-jar", "location-service.jar"]