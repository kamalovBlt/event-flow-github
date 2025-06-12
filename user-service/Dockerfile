FROM eclipse-temurin:21-jre

WORKDIR /event-flow

COPY user-impl/build/libs/user-impl-1.0-SNAPSHOT.jar user-service.jar

CMD ["java", "-jar", "user-service.jar"]