# ---- Build Stage ----
FROM gradle:9.6.1-jdk21-alpine AS build

# Install Python and PyYAML
RUN apk add --no-cache python3 py3-yaml

WORKDIR /app

# Copy only build files first for caching
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle gradle

# Make gradlew executable
RUN chmod +x ./gradlew

# Download dependencies only
RUN ./gradlew --no-daemon dependencies || true

# Copy the generator script directory
COPY scripts scripts

# Copy source code
COPY src src

# Copy the checked-out k8s-deployments folder into the container
COPY k8s-deployments /k8s-deployments

ENV LOCALIZATION_YAML_PATH=/k8s-deployments/apps/faibot/i18n/de_DE.yml
RUN ./gradlew --no-daemon shadowJar

# ---- Runtime Stage ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar faibot.jar
ENTRYPOINT ["java", "-jar", "faibot.jar"]
