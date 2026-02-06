# Usamos Java 17
FROM eclipse-temurin:17-jdk

# Carpeta de trabajo dentro del contenedor
WORKDIR /app

# Copiamos todo el proyecto
COPY . .

# Compilamos la app
RUN ./mvnw clean package -DskipTests

# Render usa la variable PORT
ENV PORT=8080

# Exponemos el puerto (informativo)
EXPOSE 8080

# Arranque de la app
CMD ["sh", "-c", "java -jar target/*.jar"]
