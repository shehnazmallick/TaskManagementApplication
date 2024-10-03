FROM openjdk:22-jdk-slim
LABEL authors="shehnaz"

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
COPY build/libs/TaskManagementApplication-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your Spring Boot app listens on
EXPOSE 8080

# Command to run your Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]