# Etapa 1: Build del proyecto
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar descriptor de dependencias
COPY pom.xml .

# Descargar dependencias para aprovechar caché de Docker
RUN mvn dependency:go-offline -B

# Copiar código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final de ejecución (Alpine JRE ligera)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar el archivo JAR generado
COPY --from=build /app/target/f1-ticketing-legacy-soap-*.jar app.jar

# Configuración de puerto por defecto
ENV PORT=8080
EXPOSE 8080

# Ejecución de la aplicación
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
