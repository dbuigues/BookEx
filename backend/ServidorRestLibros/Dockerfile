# 1. Imagen Maven + Java
FROM maven:3.9.5-eclipse-temurin-17 AS build

WORKDIR /app

# Copiamos el proyecto
COPY . .

# Compilamos
RUN mvn clean package -DskipTests

# 2. Imagen Java más ligera
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copiamos el .jar desde la fase anterior
COPY --from=build /app/target/*.jar app.jar

# Puerto
ENV PORT=8080
EXPOSE 8080

# Arranque
CMD ["sh", "-c", "java -jar app.jar"]
