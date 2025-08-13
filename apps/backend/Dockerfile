# ========================================
# 1. 빌드 스테이지 (Builder Stage)
# =======================================
# Gradle과 JDK가 포함된 이미지를 빌드 환경으로 사용
FROM gradle:jdk21-ubi-minimal AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper와 빌드 스크립트를 먼저 복사하여 의존성 레이어 캐싱
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY src ./src
COPY .gradle /app/.gradle

# Copy the Gradle cache from the build context
COPY build-cache-home /root/.gradle

ENV GRADLE_USER_HOME /root/.gradle

RUN chmod +x ./gradlew

# Set GRADLE_USER_HOME to the copied cache location


# Gradle Wrapper가 필요한 Gradle 배포판을 다운로드하도록 합니다.
# 이 단계는 캐시될 수 있어 다음 빌드부터는 다운로드를 건너뛸 수 있습니다.
RUN ./gradlew --no-daemon --version

# Gradle 빌드 실행. --no-daemon 옵션은 CI 환경에서 권장됩니다.
# build가 성공하면 build/libs/ 에 .jar 파일이 생성됩니다.
RUN ./gradlew build --no-daemon -x test

# =========================================
# 2. 테스트 스테이지 (Tester Stage)
# =========================================
FROM builder AS tester

# 테스트 실행
RUN ./gradlew test --no-daemon

# =========================================
# 3. 최종 실행 스테이지 (Runner Stage)
# =========================================
# JDK가 아닌 JRE(Java Runtime Environment)만 포함된 훨씬 가벼운 이미지를 사용
FROM bellsoft/liberica-openjre-debian:24

WORKDIR /app

# 보안을 위해 non-root 유저 생성 및 사용
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

# 빌드 스테이지에서 생성된 실행 가능한 .jar 파일만 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 애플리케이션 포트 노출 (application.properties/yml의 server.port와 일치)
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java","-jar","app.jar"]