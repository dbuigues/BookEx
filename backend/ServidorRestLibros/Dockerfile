# ====== Stage 1: Build con Maven ======
FROM maven:3.9.5-eclipse-temurin-17 AS build

# Carpeta de trabajo dentro del contenedor
WORKDIR /app

# Copiamos todo el proyecto (pom.xml + src)
COPY . .

# Compilamos la app
RUN mvn clean package -DskipTests

# ====== Stage 2: Imagen ligera para correr la app ======
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copiamos el jar desde el build
COPY --from=build /app/target/*.jar app.jar

# Puerto dinámico para Render
ENV PORT=8080
EXPOSE 8080

# Arranque de la app
CMD ["sh", "-c", "java -jar app.jar"]
