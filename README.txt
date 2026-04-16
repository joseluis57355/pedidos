# Microservicio pedidos

Stack tecnologico:
- Java 21
- Spring Boot
- PostgreSQL
- Docker
- REST API

Funcionalidades:
- Ordenes CRUD 
- Arquitectura Limpia
- API REST con Spring Boot
- Persistencia con PostgreSQL
- Documentación con Swagger
- Validaciones con Bean Validation
- DTO + Mapper pattern
- Manejo global de excepciones
- Dockerización con Docker
- Orquestación con Docker Compose
- Arquitectura preparada para microservicios

-Despliegue en AWS: http://18.101.110.59:8080/swagger-ui/index.html

-------------------------------------------------------------------------------------------------------------------------------

Como ejecutar el proyecto:
-Requisitos:
  - Docker
  - PostgreSQL (Si se utiliza otra, deberá cambiar la base de datos a utilizar en los archivos de configuración docker-compose.yml, pom.xml y application.properties)
-Paso a paso:
  1- Crear una base de datos y asignar el nombre, usuario y contraseña en los archivos docker-compose.yml y application.properties, por defecto he dejado una base de datos llamada orders, y el usuario postgres, pero no hay ninguna contraseña asignada
  2- Compilar en la raiz con ".\mvnw clean package" (Windows)
  3- Montar el servicio en docker con "docker compose up --build"
  4- Abrir el servidor con el puerto asignado, si se ha hecho en local con la configuracion por defecto deberia verse en "http://localhost:8080/swagger-ui/index.html"
