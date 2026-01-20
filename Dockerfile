FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY build/libs/*SNAPSHOT.jar app.jar
EXPOSE 8080

# 환경 변수를 자바 실행 인자로 직접 전달하도록 변경
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "app.jar"]