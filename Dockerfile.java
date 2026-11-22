FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY Gymgroup/ .
COPY pom.xml .
COPY src ./src
RUN chmod +x mvnw || true
RUN ./mvnw clean package -DskipTests || mvn clean package -DskipTests
