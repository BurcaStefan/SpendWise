FROM eclipse-temurin:25-jre

WORKDIR /app

COPY target/SpendWise-0.0.1-SNAPSHOT.jar .

EXPOSE 9090

ENV SERVER_PORT=9090

ENTRYPOINT ["java", "-jar", "/app/SpendWise-0.0.1-SNAPSHOT.jar"]
