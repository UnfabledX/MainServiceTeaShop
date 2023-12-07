FROM eclipse-temurin:17-jdk-alpine AS builder
ARG JAR_FILE=target/FileManagerService-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]

FROM tomcat:11.0-jdk21-temurin-jammy

COPY TeaShopApplication.war /usr/local/tomcat/webapps/