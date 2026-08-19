# Multi-stage build for efficient caching and smaller image footprint
FROM eclipse-temurin:17-jre-alpine
LABEL Author="Oleksii Synelnyk"
# Set the working directory in the container
WORKDIR /app
# Copy the WAR file into the ../app/app.war directory
COPY target/*.war app.war
# Make port 8080 available to the world outside this container (8888 port for google authorization)
EXPOSE 8080 8888
# Run an application within the embedded Tomcat server
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.war"]