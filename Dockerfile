# Use Eclipse Temurin JRE (smaller image for runtime)
FROM eclipse-temurin:21-jre

# Ensure Java is in PATH (explicitly set JAVA_HOME)
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Set the working directory in the container
WORKDIR /app

# Copy the packaged jar file into the container at /app
COPY target/spring-bot-v0.1.1.jar /app

# Make port 8080 available to the world outside this container
# EXPOSE 8080

# Create logs directory
RUN mkdir -p /app/logs

# Run the jar file
ENTRYPOINT ["java", "-jar", "spring-bot-v0.1.1.jar"]