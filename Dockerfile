ARG JDK_VERSION=21

############################
# 빌드 스테이지
############################
FROM eclipse-temurin:${JDK_VERSION}-jdk AS builder
WORKDIR /app

COPY src ./src

RUN ./gradlew clean build -x test

############################
# 런타임 스테이지
############################
FROM eclipse-temurin:${JDK_VERSION}-jre AS runtime
WORKDIR /app

# 루트 사용자가 아닌 일반 사용자로 실행
RUN useradd -u 10001 spring
USER 10001

COPY --from=builder /app/build/libs/*.jar app.jar

ENV TZ=Asia/Seoul

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]