# 1단계: 빌드 스테이지 (Gradle 빌드 수행)
FROM gradle:7.6-jdk17 AS build
WORKDIR /app

# 빌드에 필요한 소스 코드 및 설정 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# gradle 실행 권한 부여 및 JAR 파일 빌드 (테스트는 제외)
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar -x test

# 2단계: 실행 스테이지 (용량이 작은 JRE 환경에서 JAR만 실행)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# 1단계 빌드 컨테이너에서 생성된 jar 파일만 쏙 빼와서 복사
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]