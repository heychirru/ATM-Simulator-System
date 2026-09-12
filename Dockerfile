# Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY src ./src
COPY database ./database
RUN mvn -q -DskipTests package

# Run
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/atm-simulator-system-2.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
