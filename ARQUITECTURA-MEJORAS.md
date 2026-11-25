# Arquitectura de observaciones, notificaciones y colegiaturas

Estas mejoras siguen la estructura existente por contextos (care, iam, providers)
y las convenciones de commands, queries, servicios, recursos y ensambladores.

## Direccion de dependencias

REST -> contratos de aplicacion -> implementaciones de casos de uso -> dominio/puertos.
Los adaptadores de infraestructura implementan los puertos e integran JPA, IAM,
Resend, tareas programadas y el archivo JSON.

- Dominio: DoctorObservation valida el texto, la autoria, las nuevas alertas y el
  cierre de la revision. ObservationNotification conserva lectura idempotente y
  el limite de cinco intentos. RegisteredDoctor valida la coincidencia del nombre.
- Aplicacion: ObservationCommandService coordina escrituras transaccionales;
  ObservationQueryService realiza lecturas; NotificationEmailDeliveryService
  coordina la cola mediante interfaces. No importan repositorios JPA, HTTP ni JSON.
- Persistencia: JpaObservationAdapter, JpaNotificationAdapter y JpaPatientReviewAdapter
  conectan los puertos con los repositorios existentes.
- Correo: ResendObservationEmailAdapter contiene el cliente HTTP, credenciales,
  formato del mensaje e idempotencia. ObservationEmailWorker solo programa el caso
  de uso. Se puede sustituir el proveedor implementando ObservationEmailSender.
- Registro medico: JsonMedicalRegistryAdapter implementa MedicalRegistry.
  IAM solicita la validacion mediante MedicalCredentialValidation y su adaptador
  ProviderMedicalCredentialAdapter, sin conocer el archivo JSON.
- REST: controladores dependen de interfaces de aplicacion. Reciben recursos,
  crean commands/queries y devuelven recursos mediante ensambladores; no serializan
  entidades JPA directamente. Se conservan los campos y rutas anteriores.

## SOLID y alcance

Responsabilidad unica: transporte, orquestacion, reglas, persistencia y proveedores
se separan. Los contratos pequenos permiten sustituir adaptadores sin reescribir
los casos de uso. Las dependencias nuevas se inyectan por constructor.

Se mantiene el estilo existente: entidades de dominio anotadas con JPA y servicios
con Spring transaccional. Esto es una separacion por capas y puertos compatible con
el proyecto, no una conversion completa de todo el repositorio a Clean Architecture
sin frameworks en dominio. Los servicios anteriores mantienen su implementacion.

## Compatibilidad y pruebas

No se agregan ni renombran columnas: no requiere otra migracion de TiDB.
Los recursos conservan los nombres JSON utilizados por Angular y los endpoints.
La persistencia de observacion y notificaciones y el cierre de alertas se realizan
en transacciones. La politica de correo permanece: cola persistente, hasta cinco
intentos, sin envio cuando faltan variables, SENT indica aceptacion del proveedor.

La carpeta src/test se mantiene vacia y excluida del seguimiento de Git por
indicacion del propietario. Las pruebas ejecutadas antes de esta eliminacion
validaron el flujo, las reglas de dominio, la cola y las dependencias entre capas.
