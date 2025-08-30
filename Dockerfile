# ---- Build Stage ----
FROM gradle:9.0.0-jdk21-alpine AS build

WORKDIR /app

# Copy only build files first for caching
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle gradle

# Download dependencies only
RUN ./gradlew --no-daemon build -x test || true

# Copy source code
COPY src src
RUN ./gradlew --no-daemon shadowJar

# ---- Runtime Stage ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar faibot.jar
ENTRYPOINT ["java", "-jar", "faibot.jar"]
