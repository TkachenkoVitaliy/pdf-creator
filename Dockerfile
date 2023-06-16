FROM maven:3.8.5-openjdk-17-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN --mount=type=cache,target=/root/.m2 mvn -f /home/app/pom.xml clean package -Dmaven.test.skip

FROM eclipse-temurin:17-jre AS jre
RUN apt-get update \
    && apt-get install -y fontconfig libfreetype6 \
    && apt-get install -y --no-install-recommends libreoffice \
    && rm -rf /var/lib/apt/lists/*

FROM jre
COPY --from=build /home/app/target/pdf-creator-0.0.1.jar /usr/local/lib/pdf-creator.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "usr/local/lib/pdf-creator.jar"]