# ---- Étape 1 : construire le jar (image lourde, jetée ensuite)
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src
# Les dépendances d'abord : cette couche reste en cache tant que pom.xml ne change pas.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q -DskipTests package

# ---- Étape 2 : l'exécuter (un JRE seul, sans Maven ni sources)
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S app && adduser -S app -G app
USER app
WORKDIR /app
COPY --from=build /src/target/backend-0.0.1-SNAPSHOT.jar app.jar
# « Aujourd'hui » est celui des athlètes : Java embarque sa propre base de fuseaux horaires.
ENV JAVA_TOOL_OPTIONS="-Duser.timezone=Europe/Paris -XX:MaxRAMPercentage=75"
EXPOSE 8080
HEALTHCHECK --interval=10s --timeout=3s --start-period=40s --retries=5 \
  CMD wget -qO- http://localhost:8080/actuator/health | grep -q '"UP"' || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
