# Use Eclipse Temurin JRE (smaller image for runtime)
FROM eclipse-temurin:21-jre-jammy

# Set the working directory in the container
WORKDIR /app

# Copy the packaged jar file into the container at /app
COPY target/spring-bot-v0.1.1.jar /app

# Make port 8080 available to the world outside this container
# EXPOSE 8080

# Create logs directory
RUN mkdir -p /app/logs

# Use the full path to java (eclipse-temurin typically installs at /opt/java/openjdk)
# This ensures java is found regardless of PATH configuration
ENTRYPOINT ["/opt/java/openjdk/bin/java", "-jar", "spring-bot-v0.1.1.jar"]