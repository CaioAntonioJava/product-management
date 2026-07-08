# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Primeiro copiamos apenas o pom.xml para cachear as dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Depois copiamos o codigo e empacotamos o jar (testes sao pulados para acelerar)
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]