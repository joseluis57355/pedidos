# GitHub Actions - Guía Paso a Paso para Principiantes

Esta es una guía completa para entender y configurar el pipeline de CI/CD por primera vez.

---

## 🤔 ¿Qué es GitHub Actions?

### La idea simple

Imagina que tienes un asistente que:
1. Vigila tu repositorio en GitHub
2. Cuando haces un `push`, el asistente **automáticamente**:
   - Compila tu código
   - Ejecuta los tests
   - Si todo ok, construye la imagen Docker
   - La publica en un registro
   - Te envía una notificación

**Eso es GitHub Actions.** Es automatización.

### Analogía del mundo real

```
Sin GitHub Actions (manual):
┌──────────────────────────────────────────────┐
│ 1. Escribo código                            │
│ 2. git push                                  │
│ 3. Me conectó a servidor                     │
│ 4. Ejecuto: mvn test                         │
│ 5. Si ok, ejecuto: docker build              │
│ 6. docker push                               │
│ 7. Actualizo mi Slack: "build ok!"           │
│ ⏱️ Total: 15-20 minutos de trabajo manual     │
└──────────────────────────────────────────────┘

Con GitHub Actions (automatizado):
┌──────────────────────────────────────────────┐
│ 1. Escribo código                            │
│ 2. git push                                  │
│ 🤖 GitHub Actions ejecuta automáticamente:  │
│ 3. Compila con mvn                           │
│ 4. Ejecuta tests                             │
│ 5. Construye Docker                          │
│ 6. Publica imagen                            │
│ 7. Notifica en Slack                         │
│ ✨ Total: 0 minutos de trabajo manual        │
│ ⏱️ Tiempo real: 3-5 minutos automáticos      │
└──────────────────────────────────────────────┘
```

---

## 📚 Conceptos Clave

### 1. **Workflow** (Flujo de trabajo)

Un workflow es un **archivo YAML** que define qué hacer. En nuestro caso es: `.github/workflows/ci-cd.yml`

```yaml
name: CI/CD Pipeline           # Nombre del workflow

on:                            # Cuándo se ejecuta
  push:
    branches: [main]           # Se ejecuta cuando hago push a main

jobs:                          # Qué se ejecuta
  build-and-test:             # Nombre del job
    runs-on: ubuntu-latest    # Dónde se ejecuta (servidor de GitHub)
    steps:                     # Pasos individuales
      - run: mvn test         # Ejecuta tests
```

### 2. **Jobs** (Trabajos)

Un job es una **serie de pasos** que se ejecutan en secuencia. En nuestro pipeline tenemos 4 jobs:

| Job | Qué hace | Cuándo se ejecuta |
|---|---|---|
| `build-and-test` | Compila + ejecuta tests | Siempre |
| `docker-build` | Construye y publica imagen Docker | Solo si está en `main` Y los tests pasaron |
| `code-quality` | Análisis SonarCloud | Siempre |
| `security-scan` | Busca vulnerabilidades | Siempre |

### 3. **Steps** (Pasos)

Un step es una **acción individual**. Ejemplos:

```yaml
- name: Checkout code                    # Paso 1: Descargar código
  uses: actions/checkout@v4

- name: Set up JDK 21                    # Paso 2: Instalar Java
  uses: actions/setup-java@v4
  with:
    java-version: '21'

- name: Run tests                        # Paso 3: Ejecutar tests
  run: mvn test
```

### 4. **Runners** (Ejecutores)

Un runner es una **máquina física** en los servidores de GitHub que ejecuta el workflow.

```yaml
runs-on: ubuntu-latest   # Usar máquina Ubuntu con las librerías más recientes
```

---

## 🔄 Flujo Visual del Pipeline

```
                    ┌─────────────────────────┐
                    │    Push a GitHub        │
                    │  (git push origin main) │
                    └────────────┬────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │   GitHub Actions       │
                    │   Detecta el push      │
                    └────────────┬────────────┘
                                 │
        ┌────────────────────────┼────────────────────────┐
        │                        │                        │
        ▼                        ▼                        ▼
    ┌─────────────────┐    ┌──────────────┐    ┌──────────────────┐
    │ build-and-test  │    │ code-quality │    │  security-scan   │
    └────────┬────────┘    └──────────────┘    └──────────────────┘
             │
      ¿Tests ok?
        /      \
      SÍ        NO
      │         │
      │         └──▶ ❌ Pipeline FALLA
      │
      ▼
    ┌───────────────────────┐
    │ docker-build          │
    │ (solo en main)        │
    └────────┬──────────────┘
             │
    ┌────────▼──────────┐
    │ Docker build &    │
    │ push a ghcr.io    │
    └────────┬──────────┘
             │
    ┌────────▼──────────┐
    │ Slack notify      │
    │ "Build SUCCESS"   │
    └───────────────────┘
```

---

## 🛠️ Nuestro Pipeline Explicado Paso a Paso

### Job 1: `build-and-test`

**¿Qué hace?**
- Descarga el código
- Instala Java 21
- Compila con Maven
- Ejecuta los tests
- Sube los resultados

**Pasos internos:**

```yaml
steps:
  - name: Checkout code
    uses: actions/checkout@v4
    # 📥 Descarga el código del repo en la máquina virtual

  - name: Set up JDK 21
    uses: actions/setup-java@v4
    with:
      java-version: '21'
    # ☕ Instala Java 21 (como si ejecutaras: apt-get install java-21)

  - name: Build with Maven
    run: mvn clean package -DskipTests
    # 🔨 Compila: mvn clean package (ignora tests por ahora)

  - name: Run tests
    run: mvn test
    # 🧪 Ejecuta tests: mvn test
    # Si ALGÚN test falla, el job falla y el pipeline se detiene

  - name: Upload test results
    if: always()
    uses: actions/upload-artifact@v4
    # 📤 Guarda los resultados (funciona incluso si falló)
```

**Salida esperada:**
```
✅ [INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
✅ Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
✅ BUILD SUCCESS
```

### Job 2: `docker-build`

**¿Cuándo se ejecuta?**
```yaml
needs: build-and-test           # Solo si build-and-test fue exitoso
if: github.event_name == 'push' && github.ref == 'refs/heads/main'
# Solo si fue un push a la rama main
```

**¿Qué hace?**
1. Compila Maven de nuevo
2. Construye imagen Docker
3. La publica en GitHub Container Registry (ghcr.io)

**Pasos:**
```yaml
- name: Log in to GitHub Container Registry
  uses: docker/login-action@v3
  # 🔑 Se autentica con GitHub token

- name: Build and push Docker image
  uses: docker/build-push-action@v5
  with:
    context: .
    push: true
    tags: ghcr.io/joseluis57355/pedidos:latest
  # 🐳 Construye Dockerfile y lo publica
```

**Resultado:**
```
La imagen estará disponible en:
ghcr.io/joseluis57355/pedidos:latest

Para usarla:
docker pull ghcr.io/joseluis57355/pedidos:latest
docker run -p 8080:8080 ghcr.io/joseluis57355/pedidos:latest
```

### Job 3: `code-quality`

**¿Qué hace?**
Ejecuta SonarCloud para analizar calidad del código.

```yaml
- name: SonarCloud Scan
  uses: SonarSource/sonarcloud-github-action@master
  # 📊 Analiza:
  # - Cobertura de tests
  # - Code smells (problemas de diseño)
  # - Security issues (vulnerabilidades)
  # - Bugs potenciales
```

**Nota:** Si no tienes configurado SONAR_TOKEN, este job se salta (continue-on-error: true)

### Job 4: `security-scan`

**¿Qué hace?**
Escanea vulnerabilidades en las dependencias.

```yaml
- name: Run Trivy vulnerability scanner
  # 🔍 Escanea las dependencias Maven en busca de vulnerabilidades conocidas
```

---

## ✋ Pausa: Entender el Problema

**¿Por qué necesitamos todo esto?**

Imagina que trabajas en equipo:

```
Sin CI/CD:
┌─────────────────────────────────────────────────────────────┐
│ 1. Dev A: git push (código compilado en su PC)              │
│ 2. Dev B: git pull                                          │
│ 3. Dev B: mvn test → ❌ FALLA en su PC                      │
│ 4. Dev B: "¿Por qué no compiló?"                           │
│ 5. Dev A: "A mí me funciona..."                            │
│ 6. Pérdida de tiempo debuggeando                            │
└─────────────────────────────────────────────────────────────┘

Con CI/CD (nuestro caso):
┌─────────────────────────────────────────────────────────────┐
│ 1. Dev A: git push                                          │
│ 2. GitHub Actions: Ejecuta tests en servidor neutral        │
│ 3. ❌ Tests FALLAN en servidor                               │
│ 4. GitHub Actions: Notifica a Dev A                         │
│ 5. Dev A: Arregla el código antes del merge                 │
│ 6. Nadie tiene sorpresas desagradables                      │
└─────────────────────────────────────────────────────────────┘
```

---

## 🚀 Configuración Paso a Paso

### Paso 1: El archivo ya existe

✅ Nosotros ya creamos `.github/workflows/ci-cd.yml`

Verifica que existe:
```bash
cd pedidos
ls -la .github/workflows/
# Deberías ver: ci-cd.yml
```

### Paso 2: Hacer push del archivo

```bash
cd pedidos
git add .github/workflows/ci-cd.yml
git commit -m "ci: Add GitHub Actions workflow"
git push origin main
```

**¿Qué sucede?**
- GitHub detecta que hay un nuevo workflow
- **Automáticamente** ejecuta el pipeline

### Paso 3: Monitorear el pipeline en GitHub

1. Abre tu repositorio en GitHub
2. Haz clic en la pestaña **"Actions"**
3. Verás una lista con los workflows ejecutándose
4. Haz clic en el workflow más reciente

**Ejemplo de lo que verás:**

```
✅ build-and-test
   ✅ Checkout code (5s)
   ✅ Set up JDK 21 (8s)
   ✅ Build with Maven (45s)
   ✅ Run tests (12s)
   ✅ Upload test results (3s)

⏳ docker-build (en progreso)
   ✅ Checkout code (3s)
   ✅ Set up JDK 21 (7s)
   ✅ Build with Maven (42s)
   ⏳ Log in to GitHub Container Registry
```

### Paso 4: Si todo es exitoso

Verás:

```
✅ build-and-test PASSED
✅ docker-build PASSED
✅ code-quality PASSED
✅ security-scan PASSED

🎉 All checks passed!
```

---

## 📊 Estados del Pipeline

### ✅ EXITOSO (Success)

```
Todos los jobs pasaron sin errores.
Las imágenes Docker se publicaron.
La notificación en Slack fue enviada.
```

**Qué sucede después:**
- La imagen está disponible en `ghcr.io/joseluis57355/pedidos:latest`
- El código está listo para producción

### ❌ FALLÓ (Failed)

```
Algún job falló. Ejemplos:

❌ Tests fallaron
❌ Maven compilation error
❌ Docker build failed
```

**Qué verás:**
- Un ❌ rojo en el workflow
- En GitHub: PR muestra "Some checks failed"
- El código NO se puede mergear a main (si tienes branch protection)

**Cómo debuggear:**
1. Click en el job que falló
2. Click en el step que falló
3. Lee el error en los logs

Ejemplo de error:
```
❌ FAILURE!
...
[ERROR] Tests run: 1, Failures: 1, Errors: 0, Skipped: 0
[ERROR] PedidosApplicationTests.contextLoads FAILED

java.lang.IllegalStateException: Failed to load ApplicationContext
...
```

### ⏭️ SKIPPED (Saltado)

```
El job no se ejecutó porque una condición no se cumplió.

Ejemplos:
- docker-build NO se ejecuta si no estamos en rama main
- SonarCloud se salta si SONAR_TOKEN no existe
- Slack se salta si SLACK_WEBHOOK no existe
```

---

## 🔐 Configuración de Secretos (Opcional pero Recomendado)

### ¿Qué son los secretos?

Son **contraseñas/tokens** que GitHub guarda de forma segura. El workflow puede usarlos sin mostrarlos públicamente.

```yaml
# En el workflow:
env:
  SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
  # GitHub reemplaza esto con el valor secreto (sin mostrarlo)
```

### Los 3 secretos opcionales

#### 1. SONAR_TOKEN (Análisis de calidad)

**¿Para qué?** Analizar calidad del código, cobertura, etc.

**Cómo configurarlo:**

1. Ve a https://sonarcloud.io/
2. Click "Log in with GitHub"
3. Autoriza con tu cuenta
4. Crea un nuevo proyecto
5. Selecciona tu repositorio: `pedidos`
6. Sigue el wizard
7. Copia el **SONAR_TOKEN** (está en la configuración)
8. En GitHub: Settings → Secrets and variables → Actions
9. Click "New repository secret"
10. Name: `SONAR_TOKEN`
11. Value: [pega el token]
12. Click "Add secret"

**Resultado:**
- Verás reportes de calidad en https://sonarcloud.io/dashboard
- Análisis de cobertura de tests

#### 2. SLACK_WEBHOOK (Notificaciones)

**¿Para qué?** Recibir mensajes en Slack cuando termina el build.

**Cómo configurarlo:**

1. Abre tu workspace de Slack
2. Haz clic en tu nombre → Configuración del workspace
3. Apps → Gestionar apps
4. Busca "Incoming Webhooks"
5. Click "Instalar"
6. Selecciona canal (ej: `#devops`)
7. Click "Allow"
8. Copia la URL del webhook (comienza con https://hooks.slack.com)
9. En GitHub: Settings → Secrets → New secret
10. Name: `SLACK_WEBHOOK`
11. Value: [pega la URL]

**Resultado:**
```
Slack message:

🤖 Build SUCCESS for pedidos - main
repo: joseluis/pedidos
Tests: 6/6 passed ✅
Docker image: ghcr.io/joseluis/pedidos:latest
```

#### 3. AWS_ACCESS_KEY_ID + AWS_SECRET_ACCESS_KEY (Despliegue AWS)

**¿Para qué?** Subir imagen Docker a AWS ECR en lugar de GHCR

**Por ahora IGNORA esto**, no es necesario.

---

## 🎯 Tu Primera Ejecución del Pipeline

### Escenario: Haces un cambio en el código

```bash
cd pedidos

# Cambio 1: Editar un archivo
# Ejemplo: cambio en OrderService.java

git add -A
git commit -m "feat: add new validation"
git push origin main
```

**¿Qué sucede en GitHub?**

1. **Inmediatamente**, GitHub detecta el push
2. **Automáticamente** empieza a ejecutar el workflow
3. **Puedes ver en vivo** en Actions tab:

```
Workflow started: 2 seconds ago
Running: build-and-test

[Status: 🔄 In progress]

Step: Checkout code ✅ (took 3s)
Step: Set up JDK 21 ✅ (took 8s)
Step: Build with Maven ⏳ (running for 42s)
```

4. **Esperas 3-5 minutos** (depende de cuán lento sea tu conexión + servidores)

5. **Resultado final:**

```
✅ All checks passed!

[✅] build-and-test (3min 45sec)
[✅] docker-build (2min 30sec)
[✅] code-quality (1min 20sec)
[✅] security-scan (45sec)

Total time: 7min 40sec
```

6. **La imagen Docker está lista**:
   - Disponible en: `ghcr.io/joseluis57355/pedidos:latest`
   - Puedes hacer: `docker pull ghcr.io/joseluis57355/pedidos:latest`

---

## 🐛 Troubleshooting Común

### Problema 1: "Build failed: Java version not found"

**Causa:** El servidor no tiene Java 21

**Solución:** Ya está configurado en el workflow. Asegurate que `java-version: '21'` está en el step de setup.

### Problema 2: "Tests fail locally pero pasan en GitHub Actions (o viceversa)"

**Causa:** Diferencias en el entorno

**Solución:** 
- Tests locales usan `application-test.properties` con H2
- GitHub Actions ejecuta los mismos tests
- Si fallan diferente, es por configuración

### Problema 3: "Docker image no se publica"

**Causa:**
- No estamos en rama `main`, o
- Los tests fallaron, o
- No hay permisos

**Solución:**
- Asegurate que estás en rama main: `git branch`
- Verifica que los tests pasaron: mira el job `build-and-test`
- GitHub automáticamente tiene permisos con GITHUB_TOKEN

### Problema 4: "SonarCloud step se salta"

**Causa:** SONAR_TOKEN no está configurado (es opcional)

**Solución:** 
- Es normal, SonarCloud es opcional
- Si quieres usarlo, sigue el paso de configuración de secretos arriba
- Por ahora puedes ignorarlo

### Problema 5: "Error: Permission denied while trying to connect to Docker daemon"

**Causa:** El workflow intenta construir Docker sin permiso

**Solución:** No pasa en GitHub Actions (ya está configurado). Pero si lo ejecutas localmente, necesitas: `sudo docker build`

---

## 📈 Próximas Acciones

### Ahora mismo:

```bash
# 1. Asegurate que los cambios están en GitHub
git log --oneline | head -5

# 2. Ve a GitHub Actions
# https://github.com/tu-usuario/pedidos/actions

# 3. Espera a que termine el workflow
# Toma 3-5 minutos
```

### Después:

1. ✅ Ve a **Actions** → selecciona tu workflow → expande cada job para ver detalles
2. ✅ Verifica que la imagen Docker se creó: `ghcr.io/tu-usuario/pedidos:latest`
3. ✅ (Opcional) Configura SONAR_TOKEN si quieres análisis de calidad
4. ✅ (Opcional) Configura SLACK_WEBHOOK si quieres notificaciones

---

## 🎓 Resumen Visual

```
┌─────────────────────────────────────────────────────────────────┐
│                 TU FLUJO DE TRABAJO                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  git push origin main                                           │
│         │                                                       │
│         ▼                                                       │
│  ┌──────────────────────────────────────────┐                  │
│  │   GitHub Actions CI/CD Pipeline          │                  │
│  └────────┬─────────────────────────────────┘                  │
│           │                                                    │
│    ┌──────┴──────┐                                             │
│    │             │                                             │
│    ▼             ▼                                             │
│  Tests?       Code Quality?                                    │
│  ✅/❌          ✅/❌                                            │
│    │             │                                             │
│    └──────┬──────┘                                             │
│           │                                                    │
│      ¿Todos ok?                                                │
│       /       \                                                │
│      ✅       ❌                                                │
│      │         └─▶ Notifica error                              │
│      │                                                         │
│      ▼                                                         │
│  Build Docker Image                                            │
│  📦 ghcr.io/pedidos:latest                                     │
│      │                                                         │
│      ▼                                                         │
│  Slack Notification ✅                                         │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## ❓ Preguntas Frecuentes

**P: ¿Cuánto cuesta ejecutar GitHub Actions?**
R: Gratis para repos públicos. 2000 minutos/mes para repos privados.

**P: ¿Puedo ver los logs del pipeline?**
R: Sí, en GitHub Actions → selecciona el workflow → expande cada step.

**P: ¿Qué pasa si el pipeline falla?**
R: Puedes hacer fix, git push de nuevo, y se ejecuta el pipeline otra vez.

**P: ¿Puedo ejecutar el pipeline manualmente sin hacer push?**
R: Sí, puedes ir a Actions → click en el workflow → "Run workflow".

**P: ¿El pipeline ejecuta en mi computadora?**
R: No, ejecuta en servidores de GitHub (máquinas virtuales Ubuntu).

**P: ¿Puedo ver la imagen Docker que se generó?**
R: Sí, en GitHub → Packages → pedidos (si es pública).

---

**¡Ya tienes todo lo necesario!** Si tienes preguntas específicas, pregunta sin dudar.
