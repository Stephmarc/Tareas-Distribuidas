# Pruebas y procedencia

**Fecha:** 5/11/2026, America/Lima · **Windows:** grace · **Estudiante:** Jannys Graciela Navarro Acrota.

| Prueba | Resultado observado | Evidencia original |
|---|---|---|
| Maven verify | 9 módulos del reactor correctos; 3 tests, 0 fallos | [Transcript de compilación](evidencias/20261105/build-powershell.txt) · [Surefire](evidencias/20261105/maven-surefire.json) |
| Angular CLI | Build de producción correcto con Node 24.22.2 | [Transcript de compilación](evidencias/20261105/build-powershell.txt) |
| S06 | Precio real 2 × 15.50 = 31.00; cliente CRUD 201/200/200/204 | [JSON](S06/assets/s06.json) |
| Resiliencia | Catálogo realmente detenido: CARRITO, OPEN, HALF_OPEN; recuperación CLOSED | [JSON](S06/assets/resiliencia-real.json) |
| S07 propio | Registro 201, login 401/200, JWT RS256, BCrypt, roles y Resource Servers | [Auth](S07/assets/s07-auth.json) · [Resource Servers](S07/assets/s07-legacy.json) |
| S07b Keycloak | 401 sin JWT/alterado; 403 CLIENTE; 200 ADMIN; identidad desde sub | [JSON](S07/assets/s07.json) |
| S08 | Consumidores detenidos, lag positivo, recuperación y un aviso por orden | [JSON](S08/assets/s08.json) |
| S09 | Duplicados sin nuevos pagos; rechazo CANCELADA; compensación repetida ignorada | [JSON](S09/assets/s09.json) |
| S10 | PostgreSQL detenido: liveness 200, readiness 503, negocio 500; recuperación 200 | [JSON](S10/assets/s10.json) |
| S11 | UI: 48/49 pendientes -> PAGADA/CANCELADA por polling; PKCE S256 | [JSON](S11/assets/s11.json) |
| Unidad 1 | Dos instancias 8080/8086; Gateway alcanza ambas | [JSON](../Unidad1/assets/unidad1.json) |

## Capturas y archivos originales

Las capturas nativas válidas se guardan completas. El reloj de VS Code proviene de `tools/evidence-clock`, que lee la fecha real del sistema y el usuario Windows. El modo de evidencia de Angular lee el reloj del equipo y `preferred_username` del usuario autenticado. No se dibujan resultados encima de una imagen.

Los transcripts son archivos originales generados por PowerShell; una vista de estos archivos en VS Code se identifica como tal, sin afirmar que sea una foto de CMD. Kafka UI y Grafana son capturas complementarias de los servicios funcionando.

El 6 de noviembre se completaron las capturas nativas de código y resultados de S06 a S11, después de desbloquear Windows. Cada captura muestra la ventana completa de VS Code, el reloj real del equipo y el usuario Windows. Las vistas de ejecución identifican los transcripts originales de PowerShell y los reportes JSON; sus fechas de ejecución del 5 de noviembre se conservan.

[Manifest de archivos y SHA256](manifest-evidencias-20261105.json)

## Alcance académico

Se documentan pruebas locales reproducibles. No se inventa una nota, participación en clase, defensa oral ni respuestas de feedback. Los pagos son simulados y los avisos son registros locales, sin afirmar que se haya enviado un WhatsApp real. La tarea personal usa Pagatu; el Proyecto Sello grupal CASAS se presenta de forma independiente.

## Preparación y reproducción

[Pasos completos de ejecución](reproduccion.md). La verificación técnica usa cuentas locales de prueba, cuyos secretos se guardan fuera de Git.

## Capturas complementarias del 6 de noviembre

| Sesión | Código en VS Code | Resultados originales en VS Code |
|---|---|---|
| S06 | 1 | 3 |
| S07 | 1 | 2 |
| S08 | 1 | 1 |
| S09 | 1 | 1 |
| S10 | 1 | 1 |
| S11 | 1 | 1 |

## Integridad de archivos

Git conserva los bytes originales de capturas, PDF, transcripts y reportes mediante `.gitattributes`. Los hashes de evidencia se calculan sobre esos bytes. Para el código, `sha256NormalizadoLF` normaliza CRLF a LF antes de calcular SHA256; así el resultado coincide entre Windows y GitHub.
