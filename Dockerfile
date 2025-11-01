# ---------- Build stage ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw
RUN ./mvnw -B -q dependency:go-offline

COPY src/ src/
# RUN ./mvnw -B clean package -DskipTests
RUN ./mvnw -B clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar /app/app.jar

RUN useradd -m appuser
USER appuser

ENV JAVA_OPTS=""
EXPOSE 8080

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -Dserver.port=${PORT:-8080} -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -jar /app/app.jar"]
# EXPOSE 8080

# ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75.0","-jar","/app/app.jar"]