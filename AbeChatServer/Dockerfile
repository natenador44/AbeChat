# Stage 1: Build the App
FROM openjdk:21-jdk-slim AS build
WORKDIR /app
COPY src ./src
COPY build.gradle build.gradle
COPY gradle/ ./gradle
COPY gradlew gradlew
RUN ./gradlew bootJar

# Stage 2: Run the app
FROM amazoncorretto:21
WORKDIR /app
COPY --from=build /app/build/libs/*.jar abechat-server.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "abechat-server.jar"]