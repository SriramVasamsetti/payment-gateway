FROM maven:3.9-eclipse-temurin-21

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=0 /app/target/*.jar app.jar

EXPOSE 8000
CMD ["java", "-jar", "app.jar"]
