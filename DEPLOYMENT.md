# Publicar VitaLink en Render y TiDB Cloud

El backend se ejecuta en Render con Docker y la base de datos en TiDB Cloud,
un motor compatible con MySQL. La compatibilidad debe comprobarse con la
aplicación desplegada. Esta guía no migra los datos de MySQL local.

## 1. Base de datos

1. En https://tidbcloud.com crea una cuenta y una instancia **Starter gratuita**.
   Confirma que la pantalla muestre el plan gratuito antes de continuar.
2. Abre el editor SQL de la instancia y ejecuta:

   ```sql
   CREATE DATABASE IF NOT EXISTS vitalink;
   ```

3. En **Connect**, selecciona Java/JDBC y guarda el host, puerto, usuario
   completo (incluido cualquier prefijo) y contraseña. Usa los valores que
   muestre tu instancia; el puerto habitual es 4000.
4. Configura el acceso de red según las indicaciones de Connect. Si debes
   autorizar direcciones IP, utiliza las direcciones de salida que Render
   muestra para el servicio. No desactives TLS.

## 2. Código en GitHub

Publica los cambios de este proyecto en la rama que utilizará Render.
No subas contraseñas ni archivos con credenciales al repositorio.

## 3. Backend en Render

1. En https://dashboard.render.com crea un **New > Web Service** y conecta
   el repositorio de GitHub y la rama que contiene estos cambios.
2. Selecciona **Docker** y el tipo de instancia **Free**. El Dockerfile está
   en la raíz. No necesitas configurar Build Command ni Start Command.
3. Agrega estas variables en **Environment**:

   | Variable | Valor |
   | --- | --- |
   | `SPRING_PROFILES_ACTIVE` | `prod` |
   | `JAVA_TOOL_OPTIONS` | `-Xms64m -Xmx256m` |
   | `DATABASE_HOST` | Host de TiDB, sin protocolo ni puerto |
   | `DATABASE_PORT` | Puerto indicado por TiDB, normalmente `4000` |
   | `DATABASE_NAME` | `vitalink` |
   | `DATABASE_USER` | Usuario completo indicado por TiDB |
   | `DATABASE_PASSWORD` | Contraseña de TiDB; introducir solo en Render |
   | `FRONTEND_ORIGIN` | URL del frontend, sin barra final; para probar localmente `http://localhost:4200` |
   | `DATABASE_DDL_AUTO` | `update` solo para inicializar esta base nueva |

4. En Advanced configura **Health Check Path** como `/v3/api-docs`.
5. Crea el servicio y revisa los logs. Hibernate creará las tablas vacías
   mediante `update`. No uses `create` ni `create-drop`.
6. Cuando el primer arranque termine correctamente, cambia
   `DATABASE_DDL_AUTO` a `validate` y aplica el cambio con un nuevo despliegue.
   Para futuras modificaciones del esquema prepara migraciones explícitas.

Como alternativa, `render.yaml` permite crear un Blueprint. Su configuración
usa `validate` por defecto: si la base está vacía, cambia temporalmente la
variable a `update` para el primer arranque y después restaura `validate`
tanto en Render como en el Blueprint si lo modificaste.

## 4. Verificación

- Abre `https://TU-SERVICIO.onrender.com/v3/api-docs`: debe responder HTTP 200.
- Abre `https://TU-SERVICIO.onrender.com/swagger-ui/index.html`.
- Consulta `GET /api/v1/clinics`: una base nueva responde una lista vacía.
- Crea una cuenta usando el contrato de Swagger y prueba el inicio de sesión.
- Comprueba que las tablas estén en TiDB y que los datos persistan tras un
  nuevo despliegue.
- Configura el frontend para usar `https://TU-SERVICIO.onrender.com/api/v1`
  y comprueba solicitudes desde su origen autorizado.

El perfil prod no carga usuarios ni datos ficticios. Si necesitas los datos
actuales, realiza una exportación e importación aparte y comprueba los registros.

## Límites y diagnóstico

- Render Free se suspende después de 15 minutos sin tráfico; la primera
  solicitud posterior puede tardar aproximadamente un minuto.
- TiDB Starter tiene cuotas gratuitas: revisa almacenamiento y consumo.
- `Schema-validation: missing table`: falta inicializar las tablas.
- `Access denied`: revisa usuario completo, contraseña y permisos.
- Un timeout de conexión requiere revisar host, puerto y acceso de red.
- Un error de certificado requiere revisar el host y TLS; no desactives
  la verificación de identidad del servidor.
- HTTP 401 en rutas privadas es normal si falta el token de sesión.

Referencias: https://render.com/docs/docker,
https://render.com/docs/free,
https://docs.pingcap.com/developer/dev-guide-sample-application-java-jdbc/,
https://www.pingcap.com/pricing/.
