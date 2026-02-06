# ====== Stage 1: Build con Maven ======
FROM maven:3.9.5-eclipse-temurin-21 AS build

# Carpeta de trabajo dentro del contenedor
WORKDIR /app

# Copiamos pom.xml y mvnw para el build
COPY pom.xml ./
COPY mvnw.cmd ./
COPY .mvn ./.mvn

# Copiamos el código fuente
COPY src ./src

# Compilamos la app
RUN mvn clean package -DskipTests

# ====== Stage 2: Imagen ligera para correr la app ======
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copiamos el jar desde el build
COPY --from=build /app/target/*.jar app.jar

# Puerto dinámico para Render
EXPOSE 8080

# Arranque de la app con puerto configurable desde variable de entorno
CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]
