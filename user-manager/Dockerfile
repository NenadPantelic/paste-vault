# First stage: build the application
FROM maven:3.9.9-amazoncorretto-17 AS builder
WORKDIR /app
COPY pom.xml .
# download all packages, so we can cache them in a layer and speed up the
# upcoming builds
ADD /home/nenad/.m2/repository/com/pastevault/ ~/.m2/repository/com/pastevault
RUN mvn dependency:go-offline

COPY src/ ./src/
RUN mvn package -DskipTests

FROM openjdk:17-slim
LABEL maintainer="Nenad Pantelic<nenadpantelickg@gmail.com>"
LABEL version="0.0.1"
LABEL description="PasteVault user-manager service"
COPY --from=builder /app/target/user-manager-0.0.1-SNAPSHOT.jar user-manager.jar
HEALTHCHECK --interval=5s \
            --timeout=3s \
            CMD curl -f http://localhost:9801/internal/status || exit 1
ENTRYPOINT ["java", "-jar", "/user-manager.jar"]