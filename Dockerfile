# Вихідний образ із JDK
FROM openjdk:23-jdk-slim

# Директорія для застосунку
WORKDIR /app

# Копіюємо JAR файл у контейнер
COPY target/lab4-0.0.1-SNAPSHOT.jar app.jar

# Відкриваємо порт
EXPOSE 8080

# Запускаємо Spring Boot застосунок
ENTRYPOINT ["java", "-jar", "app.jar"]