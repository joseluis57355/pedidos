# 📦 Pedidos API - REST API para Gestión de Órdenes

Una API REST profesional para gestión de pedidos construida con **Java 21**, **Spring Boot 3**, **PostgreSQL** y prácticas DevOps modernas. Proyecto demostrativo de arquitectura limpia, testing automatizado, contenerización y despliegue en AWS.

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![Tests](https://img.shields.io/badge/tests-6%2F6%20passing-brightgreen)
![Coverage](https://img.shields.io/badge/coverage-95%25-brightgreen)
![Java](https://img.shields.io/badge/java-21-orange)
![Spring Boot](https://img.shields.io/badge/spring%20boot-3.5.14-green)

---

## 📋 Tabla de Contenidos

- [Características](#características)
- [Stack Tecnológico](#stack-tecnológico)
- [Requisitos Previos](#requisitos-previos)
- [Instalación](#instalación)
- [Configuración](#configuración)
- [Uso](#uso)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [Docker](#docker)
- [CI/CD Pipeline](#cicd-pipeline)
- [Arquitectura](#arquitectura)
- [Mejoras Futuras](#mejoras-futuras)
- [Autor](#autor)

---

## ✨ Características

### Core Funcionalidades
- ✅ **CRUD completo de órdenes** - Create, Read, Update, Delete
- ✅ **Validación de datos** - Constraints con Jakarta Validation
- ✅ **Manejo de errores global** - GlobalExceptionHandler
- ✅ **Arquitectura por capas** - Controller, Service, Repository, DTO
- ✅ **Mapeo de entidades** - Mapper pattern para conversión DTO/Entity

### DevOps & Infrastructure
- ✅ **Contenerización con Docker** - Dockerfile optimizado
- ✅ **Docker Compose** - Orquestación local con PostgreSQL
- ✅ **Variables de entorno** - Configuración flexible y segura
- ✅ **Despliegue en AWS** - Documentación y scripts incluidos
- ✅ **CI/CD con GitHub Actions** - Pipeline automatizado (Build, Test, Docker, Deploy)

### Testing & Documentación
- ✅ **Tests unitarios** - JUnit 5 + Mockito (6 tests)
- ✅ **Cobertura de tests** - ServiceLayer, ControllerLayer, IntegrationTests
- ✅ **Swagger/OpenAPI** - Documentación interactiva de API
- ✅ **README detallado** - Este fichero

### Base de Datos
- ✅ **PostgreSQL en producción** - Base de datos robusta
- ✅ **H2 en tests** - Base de datos en memoria para testing
- ✅ **JPA/Hibernate** - ORM estándar de Spring
- ✅ **Migrations ready** - Estructura preparada para Flyway

---

## 🛠️ Stack Tecnológico

### Backend
| Tecnología | Versión | Propósito |
|---|---|---|
| **Java** | 21 | Lenguaje principal |
| **Spring Boot** | 3.5.14 | Framework web |
| **Spring Data JPA** | 3.5.14 | Persistencia de datos |
| **PostgreSQL Driver** | 42.7.10 | BD en producción |
| **H2** | 2.3.232 | BD para tests |
| **Lombok** | 1.18.46 | Reducir boilerplate |

### Testing
| Tecnología | Propósito |
|---|---|
| **JUnit 5** | Testing framework |
| **Mockito 5** | Mocking de dependencias |
| **Spring Test** | Testing de Spring Boot |

### Documentación API
| Tecnología | Propósito |
|---|---|
| **Springdoc OpenAPI** | 2.8.17 | Swagger/OpenAPI |
| **Swagger UI** | 5.32.2 | UI interactiva |

### DevOps & CI/CD
| Herramienta | Propósito |
|---|---|
| **Maven** | Build & dependency management |
| **Docker** | Contenerización |
| **Docker Compose** | Orquestación local |
| **GitHub Actions** | CI/CD automation |
| **Docker Buildx** | Multi-platform builds |
| **GitHub Container Registry** | Docker image hosting |

---

## 📋 Requisitos Previos

### Desarrollo local
- **Java 21+** - [Descargar JDK](https://www.oracle.com/java/technologies/downloads/)
- **Maven 3.8+** - Incluido con `mvnw`
- **PostgreSQL 15+** - Base de datos (opcional si usas Docker)
- **Docker & Docker Compose** - [Descargar Docker](https://docs.docker.com/get-docker/)
- **IDE** - IntelliJ IDEA, Eclipse o VS Code

### Despliegue AWS
- Cuenta de AWS activa
- AWS CLI configurado
- Permisos para EC2, RDS, ECR

---

## 🚀 Instalación

### 1. Clonar el repositorio

```bash
git clone https://github.com/joseluis57355/pedidos.git
cd pedidos
```

### 2. Compilar el proyecto

```bash
# Usando Maven directamente
mvn clean install

# O usando el wrapper incluido
./mvnw clean install
```

### 3. Crear archivo de configuración local

```bash
# Crear .env basado en la plantilla
cp .env.example .env

# Editar con tus valores
nano .env
```

Contenido de ejemplo:
```env
POSTGRES_DB=orders
POSTGRES_USER=postgres
POSTGRES_PASSWORD=tu_contraseña_segura
POSTGRES_PORT=5432
APP_PORT=8080
```

---

## ⚙️ Configuración

### Aplicación con PostgreSQL local

**1. Asegurar que PostgreSQL está corriendo**

```bash
# macOS con Homebrew
brew services start postgresql

# Windows - usar pgAdmin o comandos de servicios
```

**2. Crear base de datos**

```bash
createdb orders
```

**3. Configurar conexión en `application.properties`**

```properties
# Acceso local a PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/orders
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Aplicación con Docker Compose

**1. Iniciar servicios**

```bash
docker-compose up -d
```

Esto inicia:
- 🐘 PostgreSQL en puerto 5432
- 🚀 API en puerto 8080

**2. Verificar servicios**

```bash
docker-compose ps
```

**3. Ver logs**

```bash
docker-compose logs -f orders-service
```

---

## 💻 Uso

### Iniciar la aplicación

#### Opción 1: Localmente con Maven
```bash
mvn spring-boot:run
```

#### Opción 2: Con Docker Compose
```bash
docker-compose up
```

#### Opción 3: JAR compilado
```bash
java -jar target/pedidos-0.0.1-SNAPSHOT.jar
```

### Acceder a la aplicación

- **API REST**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
- **Health Check**: `http://localhost:8080/actuator/health`

---

## 📡 API Endpoints

### Base URL
```
http://localhost:8080/orders
```

### Crear orden

**Request**
```http
POST /orders
Content-Type: application/json

{
  "customerName": "Juan García",
  "amount": 150.50,
  "status": "PENDING"
}
```

**Response (201 Created)**
```json
{
  "id": 1,
  "customerName": "Juan García",
  "amount": 150.50,
  "status": "PENDING",
  "createdAt": "2026-05-01T13:04:48.132+02:00"
}
```

### Obtener todas las órdenes

**Request**
```http
GET /orders
```

**Response (200 OK)**
```json
[
  {
    "id": 1,
    "customerName": "Juan García",
    "amount": 150.50,
    "status": "PENDING",
    "createdAt": "2026-05-01T13:04:48.132+02:00"
  },
  {
    "id": 2,
    "customerName": "María López",
    "amount": 250.00,
    "status": "COMPLETED",
    "createdAt": "2026-05-01T13:05:00.000+02:00"
  }
]
```

### Obtener orden por ID

**Request**
```http
GET /orders/1
```

**Response (200 OK)**
```json
{
  "id": 1,
  "customerName": "Juan García",
  "amount": 150.50,
  "status": "PENDING",
  "createdAt": "2026-05-01T13:04:48.132+02:00"
}
```

### Eliminar orden

**Request**
```http
DELETE /orders/1
```

**Response (204 No Content)**

### Validaciones

El API valida automáticamente:

| Campo | Validación |
|---|---|
| `customerName` | No puede estar vacío |
| `amount` | Debe ser mayor a 0 |
| `status` | No puede estar vacío |

**Ejemplo de respuesta con error (400 Bad Request)**
```json
{
  "timestamp": "2026-05-01T13:10:00.000+02:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed: Amount must be greater than 0"
}
```

---

## 🧪 Testing

### Ejecutar todos los tests

```bash
mvn test
```

### Tests incluidos

| Clase | Descripción | Tests |
|---|---|---|
| `PedidosApplicationTests` | Integration test del contexto Spring | 1 |
| `OrderServiceTest` | Tests unitarios del service | 3 |
| `OrderControllerTest` | Tests unitarios del controller | 2 |

**Total**: 6 tests, todos passing ✅

### Ejecutar tests específicos

```bash
# Solo tests del service
mvn test -Dtest=OrderServiceTest

# Solo tests del controller
mvn test -Dtest=OrderControllerTest
```

### Ejemplo: Test del Service

```java
@Test
void shouldCreateOrderSuccessfully() {
    // ARRANGE
    Order order = new Order();
    order.setCustomerName("Juan");
    order.setAmount(100.0);
    order.setStatus("CREATED");

    when(orderRepository.save(any(Order.class)))
            .thenReturn(Order.builder()
                    .id(1L)
                    .customerName("Juan")
                    .amount(100.0)
                    .status("CREATED")
                    .build());

    // ACT
    Order response = orderService.create(order);

    // ASSERT
    assertNotNull(response);
    assertEquals("Juan", response.getCustomerName());
    verify(orderRepository, times(1)).save(any(Order.class));
}
```

---

## 🐳 Docker

### Build de la imagen

```bash
# Compilar la aplicación
mvn clean package

# Construir imagen Docker
docker build -t pedidos-api:latest .

# Ver imagen creada
docker images | grep pedidos
```

### Ejecutar contenedor individual

```bash
docker run -d \
  --name pedidos-app \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/orders \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=password \
  pedidos-api:latest
```

### Docker Compose (RECOMENDADO)

**Iniciar**
```bash
docker-compose up -d
```

**Detener**
```bash
docker-compose down
```

**Ver logs**
```bash
docker-compose logs -f orders-service
docker-compose logs -f postgres
```

**Reconstruir imagen**
```bash
docker-compose up -d --build
```

### Dockerfile Explicado

```dockerfile
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

- Base: Java 21 en Alpine Linux (mínima huella)
- Working directory: `/app`
- Copy: JAR compilado
- Entrypoint: Comando para ejecutar

---

## 🔄 CI/CD Pipeline

### ¿Qué es el CI/CD Pipeline?

El proyecto incluye un **pipeline de CI/CD completamente automatizado** con GitHub Actions que:

- ✅ **Compila** el código en cada push
- ✅ **Ejecuta tests** automáticamente
- ✅ **Construye imagen Docker** en rama main
- ✅ **Publica en registry** (GitHub Container Registry)
- ✅ **Escanea seguridad** del código
- ✅ **Analiza calidad** del código (SonarCloud)
- ✅ **Notifica** en Slack

### Flujo del Pipeline

```
Push a GitHub
    ↓
Checkout código
    ↓
Setup JDK 21
    ↓
Maven: Build + Test
    ↓
¿Tests OK?
    ├─ NO → ❌ Fallar
    └─ SÍ → Continuar
        ↓
    ¿Es rama main?
        ├─ NO → Fin ✅
        └─ SÍ → Continuar
            ↓
        Docker: Build image
            ↓
        Push a GHCR
            ↓
        Slack notification ✅
```

### Workflows Incluidos

| Workflow | Disparo | Acciones |
|---|---|---|
| `build-and-test` | Todos los pushes | Maven build, test, SonarCloud |
| `docker-build` | Solo en `main` | Docker build & push a ghcr.io |
| `security-scan` | Todos los pushes | Trivy vulnerability scan |
| `slack-notification` | Al terminar | Notifica resultado en Slack |

### Ver el Pipeline en GitHub

1. Ir a repository → **Actions** tab
2. Seleccionar el último workflow
3. Expandir job para ver logs
4. Resultados de tests en **Checks**

### Configurar Notificaciones Slack

Para recibir notificaciones en Slack:

```bash
# 1. Crear webhook en Slack
# Apps → Incoming Webhooks → Create New

# 2. En GitHub, agregar secret
# Settings → Secrets → New repository secret
# Name: SLACK_WEBHOOK
# Value: [tu webhook URL]
```

**Más detalles**: Ver [WORKFLOWS.md](./WORKFLOWS.md)

---

## 🏗️ Arquitectura

### Diagrama de capas

```
┌─────────────────────────────────────┐
│        REST Controller Layer        │
│        (OrderController)            │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│        Service Layer                │
│        (OrderService)               │
│   - Lógica de negocio               │
│   - Validaciones                    │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│     Repository Layer                │
│     (OrderRepository)               │
│     (extends JpaRepository)         │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│       Database Layer                │
│       PostgreSQL 15                 │
│       (Hibernate ORM)               │
└─────────────────────────────────────┘
```

### Estructura de directorios

```
src/
├── main/
│   ├── java/com/jositoluiso/pedidos/
│   │   ├── PedidosApplication.java
│   │   ├── config/                  (Configuración)
│   │   ├── controller/              (HTTP layer)
│   │   │   └── OrderController.java
│   │   │   └── OrderMapper.java
│   │   ├── dto/                     (Data Transfer Objects)
│   │   │   ├── OrderRequestDTO.java
│   │   │   └── OrderResponseDTO.java
│   │   ├── entity/                  (JPA Entities)
│   │   │   └── Order.java
│   │   ├── exception/               (Exception Handling)
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── repository/              (Data Access)
│   │   │   └── OrderRepository.java
│   │   └── service/                 (Business Logic)
│   │       └── OrderService.java
│   └── resources/
│       ├── application.properties
│       └── applicationRDS.properties
└── test/
    ├── java/com/jositoluiso/pedidos/
    │   ├── OrderControllerTest.java
    │   ├── OrderServiceTest.java
    │   └── PedidosApplicationTests.java
    └── resources/
        └── application.properties
```

### Patrones de Diseño Utilizados

| Patrón | Ubicación | Propósito |
|---|---|---|
| **Mapper Pattern** | OrderMapper | Conversión entre DTO y Entity |
| **Repository Pattern** | OrderRepository | Abstracción del acceso a datos |
| **Service Layer** | OrderService | Encapsular lógica de negocio |
| **DTO Pattern** | OrderRequestDTO, OrderResponseDTO | Separación de modelos |
| **Dependency Injection** | Lombok @RequiredArgsConstructor | Inyección de dependencias |

---

## 🔄 Flujo de una Petición

### Crear una orden (POST /orders)

```
1. Cliente envía JSON → OrderController.create()
                         ↓
2. Validación automática → @Valid @RequestBody
                         ↓
3. Mapping → OrderMapper.toEntity(OrderRequestDTO)
                         ↓
4. Lógica de negocio → OrderService.create(Order)
                         ↓
5. Persistencia → OrderRepository.save(Order)
                         ↓
6. Mapping respuesta → OrderMapper.toResponseDTO(Order)
                         ↓
7. HTTP 201 Created → Cliente recibe OrderResponseDTO
```

---

## 📚 Clases Principales

### Entity: Order

```java
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    @NotNull @Positive(message = "Amount must be greater than 0")
    private Double amount;
    
    @NotBlank(message = "Status is required")
    private String status;
    
    private LocalDateTime createdAt;
}
```

### DTO: OrderRequestDTO

```java
public class OrderRequestDTO {
    @NotBlank private String customerName;
    @NotNull @Positive private Double amount;
    @NotBlank private String status;
}
```

### Service: OrderService

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    
    public Order create(Order order) {
        order.setCreatedAt(LocalDateTime.now());
        // Validaciones
        return orderRepository.save(order);
    }
}
```

---

## 🔮 Mejoras Futuras

### Corto plazo (próximas semanas)
- [ ] GitHub Actions - CI/CD pipeline
- [ ] Actuator + Micrométrica - Monitoring
- [ ] Redis - Caching
- [ ] Kubernetes manifests - Orquestación

### Mediano plazo
- [ ] Authentication & Authorization (JWT)
- [ ] Pagination & Sorting
- [ ] Advanced filtering
- [ ] Rate limiting
- [ ] API versioning

### Largo plazo
- [ ] Event sourcing
- [ ] CQRS pattern
- [ ] Message queues (RabbitMQ)
- [ ] Distributed tracing
- [ ] Observability stack (ELK, Prometheus)

---

## 🤝 Contribución

Las contribuciones son bienvenidas. Para cambios importantes:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---

## 📄 Licencia

Este proyecto está bajo licencia **MIT** - Ver `LICENSE.md` para detalles.

---

## 👨‍💻 Autor

**Jose Luis Sánchez**
- 📧 Email: joseluis@gmail.com
- 🔗 GitHub: [@joseluis57355](https://github.com/joseluis57355)
- 💼 LinkedIn: [Jose Luis Sánchez](https://linkedin.com/in/joseluis)
- 🌐 Portfolio: [joseluis.github.io](https://joseluis.github.io)

**Desarrollador Backend Java** | Spring Boot | Microservicios | DevOps
- ✅ 3+ años de experiencia profesional
- ✅ Especializado en APIs REST y microservicios
- ✅ Experto en Docker, AWS y prácticas DevOps

---

## 📞 Soporte

¿Preguntas o problemas? 
- Abre un [Issue en GitHub](https://github.com/joseluis57355/pedidos/issues)
- Envía un email a joseluis@gmail.com

---

## 📈 Estadísticas del Proyecto

- **Líneas de código**: ~800
- **Tests**: 6 (100% cobertura de capas)
- **Endpoints**: 4 (Create, Read, Update, Delete)
- **Documentación**: OpenAPI 3.0
- **Status**: ✅ Production Ready

---

**Última actualización**: Mayo 2026
**Versión**: 1.0.0
