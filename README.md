# VitaLink Backend

Para publicar el backend y la base de datos en internet, sigue [DEPLOYMENT.md](DEPLOYMENT.md).

**Paquete con soporte completo para el JSON del dashboard.** Para cargar los datos, sigue [importacion/LEEME.md](importacion/LEEME.md). No se importan automáticamente ni se borran los datos anteriores.


Backend académico ejecutable para VitaLink, adaptado del ZIP Catch-Up Platform. Es un monolito modular con IAM, Providers, Care y Shared; sigue sus convenciones de comandos, consultas, servicios de aplicación, recursos REST, assemblers y persistencia JPA.

## Tecnologías

- Java **26** (JDK, no solo JRE, para compilar).
- Maven **3.9.16**, incluido como Maven Wrapper: `mvnw` y `mvnw.cmd`.
- Spring Boot **4.0.6**, Spring Web, Validation, Spring Data JPA y Hibernate.
- MySQL **8.4** en Compose (el driver está administrado por Spring Boot).
- SpringDoc OpenAPI **3.0.3**, Swagger UI, Lombok y la estrategia de nombres del proyecto original.
- Spring Security, contraseñas BCrypt y sesiones bearer opacas persistidas con hash y vencimiento de ocho horas.
- JUnit y pruebas HTTP con servidor real. H2 se usa únicamente en las pruebas; no reemplaza MySQL para la ejecución normal.
- Dockerfile multietapa con Maven/JDK 26 y JRE 26.


```sh
docker compose up --build
```

Compose inicia MySQL, espera su healthcheck y levanta el backend en modo de desarrollo con datos ficticios. La primera ejecución descarga imágenes y dependencias, por lo que puede tardar varios minutos.

- Swagger: http://localhost:8080/swagger-ui/index.html
- Contrato OpenAPI: http://localhost:8080/v3/api-docs
- API: http://localhost:8080/api/v1

Detener con `Ctrl+C` o `docker compose down`. Los datos se conservan en el volumen de MySQL. Los puertos 3306 y 8080 deben estar libres; no ejecutar el backend nativo simultáneamente en el mismo puerto. Las contraseñas predeterminadas de Compose son exclusivamente ficticias para esta demostración local.

## Opción B: Java y Maven Wrapper en Windows

Necesitas JDK 26 y MySQL. Puedes iniciar únicamente la base de datos con:

```powershell
docker compose up -d mysql
```
