FROM openjdk:8-jre-alpine
MAINTAINER hellozjf <908686171@qq.com>

# Add the service itself
ARG JAR_FILE
ADD target/${JAR_FILE}  /app/app.jar

# Add application.properties
ADD src/main/resources/application.properties   /app/application.properties

# VOLUME /app
# EXPORT 8080

ENTRYPOINT ["/usr/bin/java", "-jar", "/app/app.jar", "--spring.config.location=file:/app/application.properties"]
