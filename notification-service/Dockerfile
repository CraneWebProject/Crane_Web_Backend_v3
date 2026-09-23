FROM openjdk:17-jdk-slim
WORKDIR /app

# 환경변수(비밀값 포함)는 이미지에 넣지 않고 실행 시점에 주입한다 (Jenkins → docker run -e)

COPY build/libs/notification-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]