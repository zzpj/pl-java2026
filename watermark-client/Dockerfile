FROM eclipse-temurin:21-jdk AS builder
WORKDIR /workspace

COPY . .
RUN chmod +x gradlew && ./gradlew :watermark-client:bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /workspace/watermark-client/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

