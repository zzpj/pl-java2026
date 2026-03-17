FROM eclipse-temurin:21-jdk AS builder
WORKDIR /workspace

COPY . .
RUN chmod +x gradlew && ./gradlew :eureka-server:bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /workspace/eureka-server/build/libs/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

