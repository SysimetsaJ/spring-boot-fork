# Use an official OpenJDK runtime as base image
FROM openjdk:17-jdk-slim

# Create a directory inside the image
VOLUME /tmp

# Argument to define where the jar file is located
ARG JAR_FILE=target/*.jar

# Copy the jar file into the container
COPY ${JAR_FILE} app.jar

# Run the application
ENTRYPOINT ["java","-jar","/app.jar"]