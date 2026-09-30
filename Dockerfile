FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY .mvn ./.mvn
COPY mvnw .
COPY pom.xml .
RUN sh ./mvnw -B dependency:go-offline
COPY src ./src
RUN sh ./mvnw -B package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build --chown=10001:10001 /workspace/target/payments-api-*.jar /app/app.jar
USER 10001:10001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
