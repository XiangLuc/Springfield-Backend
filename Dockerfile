# Étape 1 : compilation du jar avec Maven (les tests sont lancés à part, ils ont besoin d'une base)
FROM eclipse-temurin:21-jdk AS construction
WORKDIR /app
COPY .mvn .mvn
COPY mvnw pom.xml ./
# Retire les fins de ligne Windows si l'image est construite depuis une copie de travail sous Windows
RUN sed -i 's/\r$//' mvnw .mvn/wrapper/maven-wrapper.properties && chmod +x mvnw && ./mvnw -q dependency:go-offline
COPY src src
RUN ./mvnw -q package -DskipTests

# Étape 2 : image d'exécution, sans Maven ni sources
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=construction /app/target/*.jar springfield.jar
EXPOSE 8080
# Les offres gratuites limitent la mémoire : la JVM s'adapte à celle du conteneur
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "springfield.jar"]
