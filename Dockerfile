FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /bankrest

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /bankrest

COPY --from=builder /bankrest/target/*.jar /bankrest/bankrest.jar

ENTRYPOINT ["java", "-jar", "bankrest.jar"]
