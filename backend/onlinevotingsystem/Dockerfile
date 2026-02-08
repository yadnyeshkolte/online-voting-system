# Build Stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run Stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Create uploads directory
RUN mkdir -p user_uploads/profiles

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
