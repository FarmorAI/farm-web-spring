# Build stage
FROM --platform=linux/arm64 amazoncorretto:21 AS builder

WORKDIR /app

COPY . .

# ✅ 실행 권한 추가
RUN chmod +x ./gradlew

# ✅ Gradle 빌드 실행 (ARM64 환경에서)
RUN ./gradlew clean build -x test --no-daemon

# Run stage
FROM --platform=linux/arm64 amazoncorretto:21

WORKDIR /app

# ✅ 빌드된 JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]