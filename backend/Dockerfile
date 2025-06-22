FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /app
COPY pom.xml /app
RUN mvn dependency:go-offline
COPY src /app/src
RUN mvn clean package -DskipTests "-Dspring.profiles.active=docker"

FROM openjdk:17-jdk-slim
WORKDIR /app
ENV TZ=Asia/Ho_Chi_Minh
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["sh", "-c", "echo 'Waiting for Databases...'; sleep 30; exec java -jar app.jar --spring.profiles.active=docker"]
