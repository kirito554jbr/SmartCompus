    # Multi-stage Dockerfile: build with Maven, then create a small runtime image

# Stage 1: build the application using Maven and JDK
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy only pom first to leverage Docker layer cache for dependencies
COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline

# Copy source and build the application
COPY src ./src
RUN mvn -B -DskipTests package

# Stage 2: runtime image with a small JRE
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
ARG JAR_FILE=/workspace/target/*.jar
COPY --from=build ${JAR_FILE} app.jar
VOLUME /tmp
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
