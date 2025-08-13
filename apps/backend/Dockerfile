# ===================================================================
# 1. 빌드 스테이지 (Builder Stage)
#    - 소스 코드를 컴파일하고 실행 가능한 .jar 파일을 만드는 역할
# ===================================================================
FROM gradle:jdk21-ubi-minimal AS builder

# 작업 디렉토리 설정
WORKDIR /app

# --- Docker 레이어 캐싱 최적화 ---
# 1단계: 의존성 레이어 생성
# build.gradle, settings.gradle 파일이 변경될 때만 아래 RUN 명령이 다시 실행됩니다.
# 따라서 소스 코드만 변경되었을 때는 이 단계의 캐시를 그대로 재사용하여 시간을 절약합니다.
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY gradle ./gradle

RUN chmod +x ./gradlew

# 의존성만 미리 다운로드하여 별도의 Docker 레이어에 저장합니다.
RUN ./gradlew dependencies --no-daemon

# 2단계: 소스 코드 및 컴파일 레이어 생성
# 소스 코드를 복사합니다. 이 단계 이후부터는 소스 코드가 변경될 때 다시 실행됩니다.
COPY src ./src

# 소스 코드를 컴파일하고 최종 빌드를 실행합니다.
# 의존성은 이전 레이어에 캐시되어 있으므로, 여기서는 컴파일 작업에만 집중합니다.
RUN ./gradlew build --no-daemon -x test


# ===================================================================
# 2. 최종 실행 스테이지 (Runner Stage)
#    - 실제 애플리케이션을 실행하는 역할
#    - 최소한의 파일만 포함하여 가볍고 보안에 강한 이미지를 만듭니다.
# ===================================================================
FROM bellsoft/liberica-openjre-debian:24

# 작업 디렉토리 설정
WORKDIR /app

# 보안 강화를 위해 non-root 유저를 생성하고 해당 유저로 전환합니다.
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

# 빌드 스테이지에서 생성된 실행 가능한 .jar 파일만 복사합니다.
# JDK, Gradle, 소스 코드 등 불필요한 파일은 모두 버려집니다.
COPY --from=builder /app/build/libs/*.jar app.jar

# 애플리케이션 포트를 외부에 노출합니다.
EXPOSE 8080

# 컨테이너가 시작될 때 애플리케이션을 실행합니다.
ENTRYPOINT ["java","-jar","app.jar"]