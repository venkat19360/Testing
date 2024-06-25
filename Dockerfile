# Use a base image with OpenJDK 17 installed
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the jar file into the container at /app
COPY target/Springboot-Register-Download-2-0.0.1-SNAPSHOT.jar /app/

# Expose the port that the application runs on
EXPOSE 8080

# Set the entry point
ENTRYPOINT ["java", "-jar", "Springboot-Register-Download-2-0.0.1-SNAPSHOT.jar"]


