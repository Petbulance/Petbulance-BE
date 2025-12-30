FROM eclipse-temurin:17-jdk

COPY build/libs/app.jar app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app.jar"]
