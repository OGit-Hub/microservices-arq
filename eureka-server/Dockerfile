FROM maven:3.8.3-openjdk-17 as build
COPY . .
RUN mvn clean package -Dcurrent.version=1.0.0 -DskipTests
FROM openjdk:17-jdk
COPY --from=build target/*.jar eureka-server.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=dev","/eureka-server.jar"]