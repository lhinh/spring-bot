# Use the official OpenJDK image
FROM openjdk:21-jdk

# Set the working directory in the container
WORKDIR /app

# Copy the packaged jar file into the container at /app
COPY target/spring-bot-v0.1.1.jar /app

# Make port 8080 available to the world outside this container
# EXPOSE 8080

# Run the jar file
CMD ["java", "-jar", "spring-bot-v0.1.1.jar"]
VOLUME /app/logs