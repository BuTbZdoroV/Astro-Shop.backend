FROM gradle:8.4-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle build --no-daemon -x test  # Пропускаем тесты

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder app/build/libs/*-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]