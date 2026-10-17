FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY auth/build/libs/*.jar app.jar
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]