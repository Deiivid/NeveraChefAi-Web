This is a Kotlin Multiplatform project targeting Web.

### Eliminación de cuentas

La página pública `delete-account.html` permite eliminar una cuenta de
NeveraChefAI con correo, contraseña y confirmación explícita. Se autentica en
Supabase y llama a la función existente `delete-account`; las credenciales y
los tokens no se guardan en el navegador. Incluye una vía por correo para
usuarios de Google/Apple o sin acceso a su cuenta.

Configura estas variables públicas en GitHub Actions para publicar el formulario
(Settings → Secrets and variables → Actions → Variables):

- `SUPABASE_URL`: URL del mismo proyecto que utiliza la app.
- `SUPABASE_PUBLISHABLE_KEY`: clave publicable o clave antigua `anon`.
- `ACCOUNT_DELETION_RETENTION_NOTICE` (opcional): texto que sustituye los plazos
  de conservación si cambia el plan o la configuración de Supabase.

En desarrollo se pueden definir esas mismas propiedades en `local.properties`,
que está excluido de Git. El build genera `account-deletion-config.js`. Nunca
configures una clave `service_role` o secreta: la generación las rechaza. Sin
configuración pública válida, el formulario queda desactivado y se mantiene la
solicitud por correo.

El backend se mantiene en el repositorio de la app. Antes de publicar esta web,
despliega allí la actualización CORS de `supabase/functions/delete-account/index.ts`
con `supabase functions deploy delete-account`. Debe seguir verificando el JWT y
responder a `OPTIONS`, permitiendo `authorization`, `apikey` y `content-type`.
No se necesita cambiar el esquema ni conceder permisos nuevos.

La página describe el borrado definitivo, el bloqueo cuando es necesario
transferir un hogar compartido y los datos del hogar que conservan los demás
miembros. Los plazos incluidos se comprobaron el 15 de octubre de 2026: plan
Free, sin backups programados ni PITR, auditoría adicional en la base de datos
desactivada, registros de API/base de datos de 1 día y auditoría Auth de 1 hora.
Revisa el texto si cambia esa configuración. Referencias:
[Supabase](https://supabase.com/pricing) y
[requisitos de Google Play](https://support.google.com/googleplay/android-developer/answer/13327111?hl=es).

Una vez desplegada la web, usa su URL terminada en `/delete-account.html` en
Google Play Console. La app también debe ofrecer una ruta de eliminación de
cuenta. Este cambio no publica la web ni modifica Play Console.

Pruebas del flujo con respuestas simuladas, sin borrar cuentas reales:
`node --test webApp/tests/account-deletion.test.cjs`.

* [/shared](./shared/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
    - [commonMain](./shared/src/commonMain/kotlin) is for code that’s common for all targets.
    - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
      For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
      the [iosMain](./shared/src/iosMain/kotlin) folder would be the right place for such calls.
      Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./shared/src/jvmMain/kotlin)
      folder is the appropriate location.

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and
options:

- Web app:
    - Wasm target (faster, modern browsers): `./gradlew :webApp:wasmJsBrowserDevelopmentRun`
    - JS target (slower, supports older browsers): `./gradlew :webApp:jsBrowserDevelopmentRun`

### Running tests

Use the run button in your IDE's editor gutter, or run tests using Gradle tasks:

- Web tests:
    - Wasm target: `./gradlew :shared:wasmJsTest`
    - JS target: `./gradlew :shared:jsTest`

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…

We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack
channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them on [YouTrack](https://youtrack.jetbrains.com/newIssue?project=CMP).
