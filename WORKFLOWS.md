# CI/CD Pipeline - Configuración y Guía

Este documento explica cómo configurar y usar el pipeline de CI/CD con GitHub Actions.

## 📋 Flujo de CI/CD

El pipeline ejecuta automáticamente en cada push y pull request a las ramas `main` y `develop`:

```
┌─────────────────────────────────────────────────────────────┐
│              GitHub Push / Pull Request                      │
└────────────────────┬────────────────────────────────────────┘
                     │
        ┌────────────┼────────────┐
        │            │            │
        ▼            ▼            ▼
    ┌─────────┐  ┌──────────┐  ┌──────────────┐
    │ Build & │  │   Code   │  │   Security   │
    │  Test   │  │ Quality  │  │     Scan     │
    └────┬────┘  └──────────┘  └──────────────┘
         │
         ├─ ✅ Tests Pass?
         │
         ▼
    ┌─────────────────┐
    │  Docker Build   │
    │  (solo en main) │
    └────────┬────────┘
             │
             ▼
    ┌─────────────────────┐
    │  Push to Registry   │
    │ (ghcr.io / ECR)     │
    └─────────────────────┘
         │
         ▼
    ┌─────────────────────┐
    │  Slack Notification │
    └─────────────────────┘
```

## 🔧 Configuración de Secretos

### 1. GITHUB_TOKEN (Automático)

GitHub proporciona automáticamente un token para cada workflow. No requiere configuración.

**Permisos**: 
- `contents: read` - Leer el código
- `packages: write` - Escribir en GitHub Container Registry

### 2. SONAR_TOKEN (Opcional - Code Quality)

Para análisis de calidad de código con SonarCloud:

**Pasos:**
1. Ir a https://sonarcloud.io/
2. Conectar con GitHub
3. Crear proyecto
4. Copiar el **SONAR_TOKEN**
5. En GitHub → Settings → Secrets and variables → Actions
6. Crear nuevo secret: `SONAR_TOKEN`
7. Pegar el token

**En qué afecta**: Si no está configurado, el análisis SonarCloud se salta (pero el build continúa).

### 3. SLACK_WEBHOOK (Opcional - Notificaciones)

Para notificaciones en Slack cuando termina el build:

**Pasos:**
1. Ir a tu workspace de Slack
2. Apps & integrations → Incoming Webhooks
3. Create New Webhook
4. Seleccionar canal (ej: `#devops` o `#deployments`)
5. Copiar URL del webhook
6. En GitHub → Settings → Secrets and variables → Actions
7. Crear nuevo secret: `SLACK_WEBHOOK`
8. Pegar la URL

**Notificación que recibidas:**
```
Build SUCCESS for joseluis/pedidos - main
repo: joseluis/pedidos
message: ci-cd.yml
commit: abc123def456
author: joseluis
```

### 4. AWS_ACCESS_KEY_ID y AWS_SECRET_ACCESS_KEY (Opcional - Despliegue AWS)

Para pushear imagen a ECR (Elastic Container Registry) en AWS:

**Pasos:**
1. En AWS Console → IAM → Create User
2. Permisos necesarios:
   - `ecr:GetAuthorizationToken`
   - `ecr:BatchGetImage`
   - `ecr:PutImage`
   - `ecr:InitiateLayerUpload`
   - `ecr:UploadLayerPart`
   - `ecr:CompleteLayerUpload`
3. Copiar **Access Key ID** y **Secret Access Key**
4. En GitHub → Settings → Secrets and variables → Actions
5. Crear dos secretos:
   - `AWS_ACCESS_KEY_ID`
   - `AWS_SECRET_ACCESS_KEY`

## 🚀 Activar el Pipeline

### El pipeline se activa automáticamente cuando:

1. **Push a rama main**
   ```bash
   git commit -m "feat: add new endpoint"
   git push origin main
   ```
   → Ejecuta: Build → Test → Docker Build → Push

2. **Push a rama develop**
   ```bash
   git commit -m "feat: work in progress"
   git push origin develop
   ```
   → Ejecuta: Build → Test (sin Docker)

3. **Push a rama feature**
   ```bash
   git checkout -b feature/new-feature
   git commit -m "feat: implement feature"
   git push origin feature/new-feature
   ```
   → Ejecuta: Build → Test

4. **Pull Request**
   ```bash
   gh pr create --base main --head feature/new-feature
   ```
   → Ejecuta: Build → Test → Code Quality

## 📊 Monitorear Builds

### En GitHub

1. Ir a repository → **Actions** tab
2. Ver lista de workflows ejecutados
3. Click en un workflow para ver detalles
4. Expandir cada job para ver logs

### Ejemplo de workflow exitoso:

```
✅ build-and-test
   ✅ Checkout code
   ✅ Set up JDK 21
   ✅ Build with Maven
   ✅ Run tests
   ✅ Upload test results

✅ docker-build (solo en main)
   ✅ Log in to GitHub Container Registry
   ✅ Build and push Docker image

✅ code-quality
   ✅ SonarCloud Scan

✅ slack-notification
   ✅ Send Slack notification
```

## 🔍 Ver Resultados

### Test Results

Los resultados de tests se publican automáticamente:
- En el **Summary** del workflow (cuántos tests pasaron)
- En **Checks** del commit
- En el archivo de artefactos `test-results`

### Docker Image

Después del build exitoso en `main`:
- Imagen disponible en: `ghcr.io/joseluis57355/pedidos:latest`
- También con tags: `main`, `sha-abc123def456`, versión semántica

**Usar la imagen:**
```bash
docker pull ghcr.io/joseluis57355/pedidos:latest
docker run -p 8080:8080 ghcr.io/joseluis57355/pedidos:latest
```

### Code Quality

SonarCloud proporciona:
- **Coverage** - Cobertura de tests
- **Code Smells** - Problemas de calidad
- **Security Issues** - Vulnerabilidades
- **Bugs** - Bugs potenciales

Acceder en: `https://sonarcloud.io/dashboard?id=joseluis57355_pedidos`

## ⚠️ Troubleshooting

### Build falla: "No suitable Java version"
**Solución**: Maven está compilando con Java 21, verifica que `pom.xml` tiene `<java.version>21</java.version>`

### Tests fallan
**Solución**: Los mismos tests pasan localmente con `mvn test`

### Docker push falla
**Solución**: Asegurate que el workflow tiene permisos en `Settings → Actions → General → Workflow permissions`

### SONAR_TOKEN no funciona
**Solución**: Este es opcional, el pipeline continúa sin él

## 📝 Modificar el Pipeline

El workflow está en `.github/workflows/ci-cd.yml`

**Ejemplos de cambios:**

### Agregar notificación a Teams
```yaml
- name: Notify Teams
  uses: jdcargile/ms-teams-notification@v1.3
  with:
    github-token: ${{ github.token }}
    ms-teams-webhook-uri: ${{ secrets.TEAMS_WEBHOOK }}
  if: always()
```

### Ejecutar tests en todas las ramas
Cambiar en `on:`:
```yaml
on:
  push:
    branches: ['**']  # Todas las ramas
  pull_request:
    branches: ['**']
```

### Agregar despliegue automático a AWS ECS
```yaml
- name: Deploy to ECS
  run: |
    aws ecs update-service \
      --cluster production \
      --service pedidos \
      --force-new-deployment
  env:
    AWS_ACCESS_KEY_ID: ${{ secrets.AWS_ACCESS_KEY_ID }}
    AWS_SECRET_ACCESS_KEY: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
    AWS_REGION: eu-south-2
  if: github.ref == 'refs/heads/main' && success()
```

## 🎯 Próximos Pasos

1. ✅ **Commit and Push**
   ```bash
   git add .github/workflows/ci-cd.yml
   git commit -m "ci: Add GitHub Actions CI/CD pipeline"
   git push origin main
   ```

2. 🔧 **Configurar secretos** (en GitHub)
   - Opcionalmente: SONAR_TOKEN
   - Opcionalmente: SLACK_WEBHOOK
   - Opcionalmente: AWS credenciales

3. 👀 **Monitorear** en Actions tab
   - Ver el primer build ejecutarse
   - Verificar que tests pasan
   - Confirmar que Docker image se construye

4. 🚀 **Integrar en flujo de desarrollo**
   - Require que el workflow sea exitoso antes de merge en main
   - Settings → Branches → Add rule → Require status checks to pass

## 📚 Recursos Útiles

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [GitHub Container Registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry)
- [SonarCloud Setup](https://docs.sonarcloud.io/getting-started/github/)
- [Docker Build and Push Action](https://github.com/docker/build-push-action)

---

**Versión**: 1.0.0  
**Última actualización**: Mayo 2026
