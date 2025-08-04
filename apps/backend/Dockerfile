FROM gradle:jdk21-ubi-minimal AS builder
WORKDIR /app
COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
COPY src ./src
RUN chmod +x ./gradlew
# 👇 build 대신 bootJar를 사용하여 실행 가능한 JAR를 만듭니다.
RUN ./gradlew bootJar --no-daemon

# 👇 빌드된 JAR 파일의 레이어를 추출합니다.
WORKDIR /app/extractor
RUN java -Djarmode=layertools -jar /app/build/libs/*.jar extract

# =========================================
# 2. 최종 실행 스테이지 (Runner Stage) - 크게 변경
# =========================================
FROM eclipse-temurin:21-jre
WORKDIR /app

# 보안을 위해 non-root 유저 생성 및 사용
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

# 빌드 스테이지에서 추출된 레이어들을 순서대로 복사
COPY --from=builder /app/extractor/dependencies/ ./
COPY --from=builder /app/extractor/spring-boot-loader/ ./
COPY --from=builder /app/extractor/snapshot-dependencies/ ./
COPY --from=builder /app/extractor/application/ ./

# 애플리케이션 포트 노출
EXPOSE 8080

# 👇 실행 방식을 JarLauncher로 변경
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]