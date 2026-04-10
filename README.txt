# Microservicio pedidos

Stack tecnologico:
- Java 21
- Spring Boot
- PostgreSQL
- Docker
- REST API

Funcionalidades:
- CRUD orders
- Dockerized
- Clean architecture
- Swagger

Trabajo en proceso:
Estoy aprendiendo como funciona AWS y haciendo pruebas para tener el servicio en una instancia EC2, conectado a una base de datos Aurora(RDS). 

------------------------------------------------------------------------------------------------------------------------------------------------------

Como ejecutar el proyecto:
-Requisitos:
  - Docker
  - PostgreSQL (Si se utiliza otra, deberá cambiar la base de datos a utilizar en los archivos de configuración docker-compose.yml, pom.xml y application.properties)
-Paso a paso:
  1- Crear una base de datos y asignar el nombre, usuario y contraseña en los archivos docker-compose.yml y application.properties, por defecto he dejado una base de datos llamada orders, y el usuario postgres, pero no hay ninguna contraseña asignada
  2- Compilar en la raiz con ".\mvnw clean package" (Windows)
  3- Montar el servicio en docker con "docker compose up --build"
  4- Abrir el servidor con el puerto asignado, si se ha hecho en local con la configuracion por defecto deberia verse en "http://localhost:8080/swagger-ui/index.html"
