FROM openjdk:17-jdk-slim
WORKDIR /app

# 환경변수 정의
ARG EUREKA_URL


# 환경변수 설정
ENV EUREKA_URL=${EUREKA_URL}


COPY build/libs/eureka-server-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]