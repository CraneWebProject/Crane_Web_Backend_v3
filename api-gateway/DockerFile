FROM openjdk:17-jdk-slim
WORKDIR /app

# 환경변수 정의 - api-gateway에 필요한 것만
ARG REDIS_HOST
ARG REDIS_PORT
ARG JWT_KEY
ARG EUREKA_URL

# 환경변수 설정
ENV REDIS_HOST=${REDIS_HOST}
ENV REDIS_PORT=${REDIS_PORT}
ENV JWT_KEY=${JWT_KEY}
ENV EUREKA_URL=${EUREKA_URL}

COPY build/libs/api-gateway-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]