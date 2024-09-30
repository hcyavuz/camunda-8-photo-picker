FROM azul/zulu-openjdk:17-jre
ARG JAR_FILE=target/*.jar
COPY ./target/photo_picker-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]