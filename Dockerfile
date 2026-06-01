# Use lightweight Java 17 runtime
FROM eclipse-temurin:17-jdk-jammy

# Set working directory
WORKDIR /app

# Copy the generated JAR
COPY target/springboot-aks-demo-0.0.1-SNAPSHOT.jar app.jar

# Expose the port (default Spring Boot)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java","-jar","/app/app.jar"]