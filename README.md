# Decisiones técnicas: DocVault

---

## Cómo usar el proyecto

### Requisitos previos

- Android Studio Hedgehog o superior
- JDK 21
- Android SDK 35 (compileSdk), min SDK 26
- Git instalado
- Dispositivo físico o emulador con biométrica registrada (huella digital o PIN como fallback)

### Clonar el repositorio

```bash
git clone https://github.com/TU_USUARIO/DocVault.git
cd DocVault
```

### Abrir en Android Studio

1. Abrir Android Studio
2. Seleccionar **File → Open** y elegir la carpeta `DocVault`
3. Esperar a que Gradle sincronice las dependencias

### Compilar y ejecutar

```bash
# Compilar el APK debug
./gradlew assembleDebug

# Instalar directamente en dispositivo/emulador conectado
./gradlew installDebug
```

O usar el botón **Run** de Android Studio con un dispositivo o emulador configurado.

### Ejecutar las pruebas

```bash
# Todas las pruebas unitarias (no requiere dispositivo)
./gradlew test

# Todas las pruebas: unitarias + instrumentadas (requiere emulador o dispositivo)
./gradlew test :app:connectedDebugAndroidTest :data:connectedDebugAndroidTest
```

### Calidad de código

La calidad del código se gestionó en cuatro niveles:

**1. ktlint — análisis estático de estilo**
Analiza el código fuente comparándolo contra un conjunto de reglas de formato Kotlin. Se ejecuta como task independiente de Gradle, no como parte de la compilación: `./gradlew assembleDebug` no lo ejecuta. Cuando encuentra una violación, reporta el archivo y la línea exacta en la consola. En el CI actúa como puerta: si hay violaciones, el pipeline falla antes de llegar al build. Está configurado en el `build.gradle.kts` raíz via `subprojects { apply(plugin = "org.jlleitschuh.gradle.ktlint") }`, lo que significa que aplica a cada módulo sin necesidad de configurarlo individualmente.

```bash
# Verificar estilo en todos los módulos
./gradlew ktlintCheck

# Corregir automáticamente
./gradlew ktlintFormat
```

**2. Inspect Code de Android Studio — análisis estático**
Se usó `Analyze → Inspect Code` sobre el proyecto completo para detectar dependencias declaradas pero no utilizadas en los `build.gradle.kts`, imports innecesarios y warnings que ktlint no cubre.

**3. Pruebas automatizadas — cobertura de comportamiento**
69 pruebas distribuidas en tres niveles (unitarias, integración con Room, instrumentadas con Espresso) garantizan que los cambios no rompen el comportamiento existente. El uso de `StandardTestDispatcher` en tests de ViewModel y Turbine en tests de Flow asegura que las pruebas son deterministas y no dependen del tiempo de ejecución.

**4. CI/CD con GitHub Actions — validación continua**
El pipeline corre `./gradlew test` en cada push y pull request a `master` y `develop`. Si los tests unitarios fallan, el build y las pruebas instrumentadas no corren. Ningún código llega a `master` sin pasar por esta validación.

### Notas importantes

- La autenticación biométrica requiere tener **huella digital registrada** en el dispositivo o emulador. Si no hay biométrica registrada, el sistema hace fallback a PIN/patrón/contraseña.
- En emuladores, configurar la huella desde **Settings → Security → Fingerprint** antes de ejecutar la app.
- La funcionalidad de cámara requiere un dispositivo físico o un emulador con cámara virtual habilitada.
- La marca de agua con geolocalización requiere permiso de ubicación. En emuladores, configurar una ubicación simulada desde las **Extended Controls** del emulador.

---

## Principios SOLID aplicados

**Single Responsibility (SRP)**: cada clase tiene una sola razón para cambiar. Si una clase hace dos cosas, un cambio en cualquiera de ellas obliga a tocarla aunque la otra no haya cambiado.

**Open/Closed (OCP)**: una clase está abierta para extensión pero cerrada para modificación. Se puede agregar comportamiento nuevo sin alterar el código que ya funciona.

**Dependency Inversion (DIP)**: los módulos de alto nivel no dependen de implementaciones concretas sino de abstracciones (interfaces). Esto permite reemplazar implementaciones sin tocar el código que las usa, tanto en producción como en tests.

**Interface Segregation (ISP)**: es preferible tener interfaces pequeñas y específicas que una interfaz grande con métodos que no todos los clientes necesitan.

**Liskov Substitution (LSP)**: cualquier implementación de una interfaz debe poder reemplazar a otra sin que el comportamiento del sistema cambie. Este principio no se aplica directamente en DocVault porque cada interfaz tiene una sola implementación en producción, pero es la base conceptual que garantiza que los fakes de test (`FakeBiometricAuthManager`, `FakeNavigator`) puedan reemplazar a las implementaciones reales sin que el código que los usa lo note.

---

## Patrones de diseño aplicados

**Repository**: oculta el origen de los datos detrás de una interfaz. El código de negocio no sabe si los datos vienen de una base de datos, un archivo o una API.

**Observer**: un componente emite cambios de estado y otros reaccionan sin ser consultados activamente. En Android moderno se implementa con `StateFlow` y `SharedFlow`.

**Use Case (Interactor)**: encapsula una operación de negocio en una clase independiente. Separa la lógica de negocio del ViewModel.

**Template Method**: define el esqueleto de un algoritmo en una clase base y deja que las subclases implementen los pasos específicos.

**Decorator**: añade comportamiento a un objeto sin modificarlo ni heredar de él.

**Factory**: centraliza la creación de objetos y sus dependencias.

**Command**: encapsula una acción como objeto, permitiendo parametrizarla y desacoplar quien la emite de quien la ejecuta.

---

## Arquitectura

### Clean Architecture + MVVM

La arquitectura se divide en tres capas: Presentación, Dominio y Datos. La regla es que las dependencias solo apuntan hacia adentro; el Dominio no conoce ni la Presentación ni los Datos.

El motivo principal fue la testabilidad. Con el Dominio como módulo Kotlin puro sin dependencias de Android, toda la lógica de negocio se prueba desde la JVM sin emulador. Esto es posible porque se aplica **DIP**: los use cases dependen de interfaces de repositorio, no de Room ni de archivos cifrados. Si el origen de datos cambia, los use cases no se modifican.

Se usó MVVM porque el `ViewModel` de Jetpack sobrevive rotaciones de pantalla sin mantener referencia a la Activity. En MVP el Presenter referencia directamente a la View; si la Activity es destruida durante una operación, esa referencia apunta a un objeto inválido.

### StateFlow y SharedFlow

Ambos implementan el patrón **Observer** pero con comportamientos distintos que responden a necesidades distintas:

`StateFlow` tiene memoria: siempre tiene un valor actual. Cuando el Fragment vuelve al foreground, recibe el último estado inmediatamente sin esperar una nueva emisión. Por eso se usa para representar el estado de la pantalla (loading, success, error).

`SharedFlow` no tiene memoria: si nadie está escuchando cuando se emite, el valor se pierde. Por eso se usa para errores y eventos de navegación: son acciones puntuales que no deben repetirse si el usuario rota la pantalla.

---

## Arquitectura modular

El proyecto tiene 13 módulos independientes. Los límites entre módulos los enforcea el compilador: si `:feature-documents` intenta importar algo de `:feature-detail`, el build falla antes de ejecutar una línea de código. Las dependencias no deseadas son imposibles por diseño.

Cada módulo de librería expone interfaces, no implementaciones. `:lib-security` expone `CryptoManager` y `BiometricAuthManager`; las features que los usan no conocen nada del Keystore ni de `BiometricPrompt`. Esto aplica **ISP**: cada módulo expone solo lo que sus clientes necesitan, y **DIP**: las features dependen de abstracciones, no de implementaciones concretas de seguridad.

Las features son **Android Library modules** y no Dynamic Feature modules porque los Dynamic Features resuelven un problema que DocVault no tiene: descarga de módulos bajo demanda desde la Play Store. Usarlos hubiera requerido `DynamicNavHostFragment` y `SplitInstallManager`, infraestructura de entrega bajo demanda que no aporta ningún beneficio para una app que tiene todas sus pantallas disponibles desde la instalación.

> **Nota:** Los Dynamic Feature modules son la elección correcta cuando la app tiene funcionalidades opcionales o de uso poco frecuente que no justifican descargarse en la instalación inicial. Casos concretos: un módulo de edición avanzada de documentos que solo usan el 10% de los usuarios, una funcionalidad de firma digital que se activa por suscripción, o un módulo de onboarding que solo se necesita una vez y puede eliminarse después. En esos escenarios, Dynamic Features reducen el tamaño del APK inicial y mejoran la tasa de instalación.

---

## Patrones de diseño en el código

### Repository

Los use cases trabajan contra la interfaz `DocumentRepository`, no contra `DocumentRepositoryImpl`. Esto aplica **OCP**: si el origen de datos cambia, por ejemplo agregar sincronización con una API, se crea una nueva implementación sin modificar los use cases que ya funcionan. En tests, la interfaz se reemplaza por un mock sin que el use case lo note.

### Use Case por operación

Cada acción de negocio tiene su propia clase: `GetDocumentsUseCase`, `AddDocumentUseCase`, `DeleteDocumentUseCase`. Aplica **SRP**: cada clase tiene una sola razón para cambiar. La alternativa, poner toda la lógica en el ViewModel, produce clases de cientos de líneas con múltiples responsabilidades, difíciles de testear y de mantener.

### Command en la navegación

Los fragments emiten `NavigationCommand.ToDetail(documentId)` en lugar de llamar `NavController` directamente. Esto resuelve un problema real de módulos: el `NavController` y los IDs del nav graph viven en `:app`, pero los fragments viven en `:feature-documents`. Si el fragment llamara al NavController directamente, `:feature-documents` dependería de `:app`, lo que crearía una dependencia circular y rompería el build.

Con el patrón **Command**, el fragment emite un comando semántico y `AppNavigator` en `:app` sabe cómo traducirlo. Agregar un nuevo destino significa añadir una subclase a `NavigationCommand`; nada existente se modifica, lo que aplica **OCP**.

### Decorator en `asResult()`

La extensión `Flow<T>.asResult()` transforma cualquier Flow en `Flow<DocVaultResult<T>>` añadiendo la emisión de `Loading` y el manejo de errores, sin modificar la fuente original. Es el patrón **Decorator** aplicado a Flows: comportamiento añadido sin herencia ni modificación.

### Template Method en clases base

`BaseFragment` define el esqueleto del ciclo de vida: `inflateBinding`, `initViews`, `observeState`. Cada subclase implementa los detalles. Centraliza el manejo correcto del ViewBinding (asignación en `onCreateView` y limpieza en `onDestroyView`) y expone `collectOnStarted {}` con `repeatOnLifecycle` correcto para que ningún Fragment lo implemente incorrectamente.

### Factory a través de Hilt

Hilt actúa como **Factory** centralizado para todas las dependencias del proyecto. En tests, `@TestInstallIn` reemplaza módulos de producción por fakes (Room en memoria, biométrica fake, navegación sin efectos secundarios) sin modificar ningún archivo de producción. Esto es posible precisamente porque el diseño aplica **DIP** en todas las capas.

---

## Librerías externas

**Hilt** sobre Koin: con Koin los errores de inyección aparecen en runtime. Con Hilt el grafo de dependencias se verifica en compilación, por lo que el build falla antes de que el error llegue al dispositivo.

**KSP** sobre KAPT: KSP procesa código Kotlin directamente sin convertirlo a Java stubs. Es hasta dos veces más rápido en compilaciones incrementales y es el estándar con Kotlin 2.0.

**MockK** sobre Mockito: Kotlin hace `final` todas las clases por defecto. Mockito requiere workarounds para mockearlas. MockK las soporta de forma nativa junto con funciones `suspend` vía `coEvery` y `coVerify`.

**Turbine** para tests de Flow: `collect {}` sin control externo cuelga si el Flow no completa. Turbine provee `awaitItem()` y `awaitComplete()` con timeouts, por lo que el test falla con un mensaje claro en lugar de colgar indefinidamente.

**Room** sobre alternativas: las queries retornan `Flow<List<T>>` que emite automáticamente cuando los datos cambian. La lista de documentos se actualiza sola al agregar o eliminar uno, sin polling ni notificaciones manuales.

**CameraX** sobre Camera2: Camera2 requiere gestionar manualmente el ciclo de vida de la cámara, rotaciones del dispositivo y diferencias entre fabricantes. CameraX abstrae todo eso con una API declarativa que funciona de forma consistente.

---

## Seguridad

**AES-256/GCM sobre AES-256/CBC**: GCM es cifrado autenticado: produce un tag de 128 bits que verifica la integridad del ciphertext. Si el archivo cifrado en disco es modificado, el descifrado lanza `AEADBadTagException` antes de devolver datos. CBC cifra pero no detecta manipulaciones; puede devolver datos corruptos sin ninguna señal de error.

**Android Keystore**: la clave de cifrado nunca sale del hardware seguro del dispositivo. Las operaciones ocurren dentro del hardware; el material de la clave nunca está accesible para el código de la app. `CryptoManager` y `BiometricAuthManager` son interfaces. Esto aplica **DIP**: en tests se reemplazan por fakes sin modificar producción.

**BIOMETRIC_STRONG sobre BIOMETRIC_WEAK**: `BIOMETRIC_WEAK` incluye reconocimiento facial por cámara frontal sin infrarrojo, vulnerable a spoofing con una foto. Para documentos confidenciales ese riesgo no es aceptable. En dispositivos sin biométrica STRONG el sistema hace fallback a PIN automáticamente.

**FLAG_SECURE**: no oculta el contenido visualmente, sino que indica al SO que bloquee activamente cualquier mecanismo de captura de pantalla. Se aplica al abrir `DetailFragment` y se elimina al destruirse para no afectar al resto de la app.

---

## Estrategia de pruebas

Se aplica la pirámide: muchas pruebas unitarias baratas, pocas instrumentadas costosas.

Las **pruebas unitarias** viven en `src/test/` de cada módulo y corren en JVM sin emulador. Cubren ViewModels (`DocumentsViewModelTest`, `DetailViewModelTest`), Use Cases (`GetDocumentsUseCaseTest`, `AddDocumentUseCaseTest`, entre otros), Repositorios (`DocumentRepositoryImplTest`, `AccessLogRepositoryImplTest`) y Mappers (`DocumentMapperTest`, `AccessLogMapperTest`). Son posibles porque los ViewModels y Use Cases dependen de interfaces como `DocumentRepository`, `AccessLogRepository`, `DocVaultLocationManager` y `DocumentFileManager`, no de sus implementaciones concretas. En tests, esas interfaces se reemplazan por mocks con MockK.

Las **pruebas de integración con Room** viven en `data/src/androidTest/` y usan la base de datos real en memoria (`DocumentDaoTest`, `AccessLogDaoTest`). Verifican lo que los mocks no pueden garantizar: que las queries SQL son correctas, que los índices existen y que las restricciones de foreign key se cumplen.

Las **pruebas instrumentadas con Espresso** viven en `app/src/androidTest/` y verifican el comportamiento visible al usuario (`DocumentsFragmentTest`, `DetailFragmentTest`). Usan `@TestInstallIn` de Hilt para reemplazar tres interfaces de producción por fakes: `DocVaultDatabase` por una base de datos Room en memoria, `BiometricAuthManager` por `FakeBiometricAuthManager` que llama `onSuccess()` inmediatamente, y `Navigator` por `FakeNavigator` sin efectos secundarios de navegación.

---

## Mejoras futuras

Esta sección recoge lo que se podría incorporar al proyecto para aumentar la calidad y cobertura.

**Snapshot tests con Paparazzi.** Paparazzi renderiza los layouts directamente en JVM sin emulador y compara el resultado contra una imagen de referencia guardada en el repositorio. Si un cambio de UI afecta visualmente a una pantalla, el test falla mostrando el diff. Son especialmente útiles para detectar regresiones visuales en componentes del design system como `DocVaultToolbar`, `DocVaultButton` o `DocVaultErrorView`.

**Clave de cifrado por documento.** Actualmente todos los documentos comparten la clave maestra `docvault_master_key` en el Keystore. Una mejora de seguridad sería generar una clave distinta por documento, cifrada con la clave maestra y almacenada junto al archivo. Así, comprometer un documento no expone los demás.

**Actualización a versiones estables de biométrica.** La versión `1.2.0-alpha05` de `androidx.biometric` es una alpha. Cuando llegue a estable conviene migrar para tener soporte oficial y evitar APIs que puedan cambiar entre versiones.

**Paginación con Paging 3.** Actualmente `DocumentDao.getAllDocuments()` retorna un `Flow<List<DocumentEntity>>` que carga todos los documentos en memoria de una vez. Con 10 o 20 documentos no hay problema, pero con cientos la query carga todo en RAM simultáneamente. Paging 3 integra nativamente con Room y permite que el DAO retorne un `PagingSource<Int, DocumentEntity>`, de modo que Room carga solo los documentos visibles en pantalla más un buffer y va cargando más a medida que el usuario hace scroll. El `RecyclerView` recibe un `PagingData` que maneja automáticamente los estados de carga de páginas adicionales.

**Soporte multi-cuenta.** Actualmente hay una sola base de datos (`docvault_database`) y una sola clave AES (`docvault_master_key`) en el Keystore. Si dos personas usan la app en el mismo dispositivo, comparten todos los documentos y la misma clave de cifrado. Con multi-cuenta, cada usuario tendría su propia base de datos (ej. `docvault_database_user1`) y su propia clave en el Keystore (ej. `docvault_key_user1`). El cambio principal estaría en `DatabaseModule` y `CryptoManagerImpl`, que recibirían el identificador del usuario activo para construir el nombre de la DB y el alias de la clave. Los repositorios y use cases no cambiarían.

**Sincronización en la nube.** El patrón Repository facilita esta extensión. Actualmente `DocumentRepositoryImpl` accede directamente a `DocumentDao`. Si se agrega sincronización, el repositorio necesitaría dos fuentes de datos abstraídas detrás de interfaces: una `LocalDataSource` que encapsula las operaciones con Room, y una `RemoteDataSource` que encapsula las operaciones con la API remota. El repositorio decidiría internamente cuál usar según el contexto: leer del caché local, escribir en ambas o sincronizar cuando hay conectividad. Los use cases y ViewModels no sabrían ni necesitarían saber de dónde vienen los datos, que es exactamente el propósito del patrón Repository.

**Modularización completa de tests.** Se intentó mover `DocumentsFragmentTest` y `DetailFragmentTest` a sus módulos correspondientes (`feature-documents` y `feature-detail`), pero no funcionó. El problema es que `@TestInstallIn` en un módulo de librería solo tiene efecto si ese módulo está en el classpath como `implementation`, no como `androidTestImplementation`. Al tener `:test-utils` como `androidTestImplementation`, los módulos Hilt de test declarados ahí no son descubiertos automáticamente por el test runner de cada feature, lo que causaba que las dependencias no se inyectaran correctamente y los tests crasheaban al iniciar.

Como solución intermedia se creó el módulo `:test-utils` para centralizar los fakes (`FakeBiometricAuthManager`, `FakeNavigator`) y reducir la duplicación entre módulos, pero los tests instrumentados tuvieron que quedarse en `:app/androidTest` donde sí tienen acceso completo y `@TestInstallIn` funciona correctamente. La solución definitiva requiere configurar un test runner propio por módulo con su propia infraestructura Hilt, lo que quedó pendiente por el scope del reto.

---

*DocVault - Reto técnico | Brayan Muñoz Campos*