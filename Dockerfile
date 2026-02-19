# Use official OpenJDK 21 as base image
FROM eclipse-temurin:21-jdk-jammy

# Set working directory inside container
WORKDIR /app

# Copy the compiled JAR file into container
COPY target/*.jar app.jar

# Expose port 8080 (Bookstore API default port)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]