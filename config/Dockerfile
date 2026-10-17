FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY build/libs/*-SNAPSHOT.jar app.jar
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]