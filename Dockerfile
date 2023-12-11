FROM gradle:8.3 AS build

WORKDIR /app

COPY build.gradle .
COPY settings.gradle .
COPY src ./src

RUN gradle build -x test

FROM openjdk:17-jdk-slim


WORKDIR /app

COPY --from=build /app/build/libs/train-ticket-sales-app-0.0.1-SNAPSHOT.jar application.jar

EXPOSE 8080

CMD ["java", "-jar", "application.jar"]