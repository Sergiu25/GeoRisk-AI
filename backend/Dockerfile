FROM eclipse-temurin:21-jdk

WORKDIR /work/

COPY pom.xml ./
COPY src ./src

RUN apt-get update && apt-get install -y maven && mvn -q -DskipTests package

EXPOSE 8081

CMD ["java", "-jar", "target/quarkus-app/quarkus-run.jar"]
