FROM maven:3.8.3-openjdk-17 as build
COPY . .
RUN mvn clean package -Dcurrent.version=1.0.0 -DskipTests
FROM openjdk:17-jdk
COPY --from=build target/*.jar client-ms.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=dev","/client-ms.jar"]