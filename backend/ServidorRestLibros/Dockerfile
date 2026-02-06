# ====== Stage 1: Build con Maven ======
FROM maven:3.9.5-eclipse-temurin-21 AS build

# Carpeta de trabajo dentro del contenedor
WORKDIR /app

# Copiamos primero el pom.xml para aprovechar la cache de Docker
COPY pom.xml .

# Descargamos las dependencias
RUN mvn dependency:go-offline -B

# Copiamos el resto del proyecto
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
