FROM tomcat:10.0
LABEL Author="Oleksii Synelnyk"
# Set the working directory in the container
WORKDIR /usr/local/tomcat
# Copy the WAR file into the Tomcat webapps directory
COPY target/TeaShop-0.0.2.war /usr/local/tomcat/webapps/teashop.war
# Make port 8080 available to the world outside this container
EXPOSE 8080 8888
# Run Tomcat server
CMD ["catalina.sh", "run"]