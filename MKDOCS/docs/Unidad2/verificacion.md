# Pruebas del avance personal

**Jannys Graciela Navarro Acrota** · 29 de octubre de 2026, America/Lima.

| Comprobación | Resultado |
| --- | --- |
| Maven verify del conjunto | BUILD SUCCESS; 3 pruebas aprobadas |
| Feign: dos productos de S/ 15.50 | Total S/ 31.00 |
| Catálogo realmente detenido | Carritos 19–23, total null |
| Circuit Breaker | OPEN → HALF_OPEN → CLOSED |
| Gateway sin token / firma alterada | 401 / 401 |
| CLIENTE en ruta administrativa | 403 |
| JWT temporal: registro / login malo / login bueno | 201 / 401 / 200 |
| Contraseña y firma | BCrypt / RS256 |
| Flujo Kafka | Orden 1 PAGADA; orden 2 CANCELADA |
| Duplicados | Ignorados; una fila por orden |
| Prometheus y Loki | Métricas y logs reales |
| Angular + Keycloak | Login PKCE; órdenes 3 PAGADA y 18 CANCELADA sin recarga |

## Archivos de evidencia

- [Caída real y recuperación del catálogo](evidencias/resiliencia-real.json).
- [Respuestas de seguridad y creación de órdenes](evidencias/pruebas-api.json).
- [Registro y login del JWT temporal](evidencias/autenticacion-temporal.json).
- [Estados de pago](evidencias/estados-pago.json).
- [Duplicados ignorados](evidencias/duplicados.txt).
- [Correlación entre servicios](evidencias/trazabilidad.txt).

## Alcance

Esta es la tarea personal, no el informe grupal oficial. Se documenta lo realmente ejecutado. No se añaden autoevaluaciones personales ni pruebas de consumidor o base de datos apagados que no se realizaron. Las capturas del navegador no incluyen la barra de tareas de Windows; si el docente exige literalmente reloj y perfil del sistema, habrá que tomar esa captura desde el escritorio.

El frontend local fue compilado con Angular y empaquetado con esbuild WASM por compatibilidad con el entorno del asistente. El build estándar de Angular CLI queda preparado en el proyecto. El push sigue requiriendo autenticación de GitHub desde la sesión de Windows.
