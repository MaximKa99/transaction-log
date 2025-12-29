# Stage 1: Build the Gradle project
FROM amazoncorretto:17 AS build
WORKDIR /app

COPY src ./src
COPY gradle ./gradle
COPY build.gradle.kts ./build.gradle.kts
COPY settings.gradle.kts ./settings.gradle.kts
COPY gradlew ./gradlew

RUN sed -i 's/\r$//' gradlew && chmod +x gradlew
RUN ./gradlew clean build -x test

# Stage 2: Prepare runtime environment and run the application
FROM amazoncorretto:17

WORKDIR /app

COPY --from=build /app/build/libs/transaction-log-1.0-SNAPSHOT.jar app.jar

EXPOSE 50005

ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:50005", "-jar", "app.jar"]
