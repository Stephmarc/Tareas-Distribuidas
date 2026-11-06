# Unidad 2 · Tarea personal

**Jannys Graciela Navarro Acrota · Casa o Nada**

Sesiones 6 a 11 sobre la implementación individual de Pagatu. Las pruebas y capturas nuevas corresponden al **5 de noviembre de 2026**, hora de Lima. La entrega grupal CASAS se mantiene separada.

| Sesión | Informe y evidencias | PDF |
|---|---|---|
| S06 | [Comunicación síncrona resiliente](S06/index.md) | [Descargar](S06/S06_Equipo09_NavarroJannys.pdf) |
| S07 | [Seguridad distribuida y roles](S07/index.md) | [Descargar](S07/S07_Equipo09_NavarroJannys.pdf) |
| S08 | [Mensajería asíncrona](S08/index.md) | [Descargar](S08/S08_Equipo09_NavarroJannys.pdf) |
| S09 | [Idempotencia y compensación](S09/index.md) | [Descargar](S09/S09_Equipo09_NavarroJannys.pdf) |
| S10 | [Observabilidad y diagnóstico](S10/index.md) | [Descargar](S10/S10_Equipo09_NavarroJannys.pdf) |
| S11 | [Angular, Keycloak y estado asíncrono](S11/index.md) | [Descargar](S11/S11_Equipo09_NavarroJannys.pdf) |

## Qué encontrarás

Cada sesión incluye los cuatro bloques de la guía, los archivos de código y sus líneas, comandos reproducibles, respuestas originales, capturas explicadas, un hallazgo real y reflexión técnica. Los archivos `.txt` y `.json` se pueden descargar; no se presentan rótulos de aprobación como si fueran capturas de ejecución.

[Ver matriz de pruebas y procedencia](verificacion.md) · [Revisión de Unidad 1](../Unidad1/auditoria.md)

[Consultar los pasos completos, incluido el perfil auth de S07](reproduccion.md).

## Ejecutar en PowerShell

Desde la carpeta del repositorio, con Docker Desktop iniciado:

```powershell
& ".\scripts\Start-Dev.ps1" -Observabilidad
node .\scripts\Verify-Personal.cjs setup
& ".\scripts\Run-PersonalEvidence.ps1" -Sesion s06
& ".\scripts\Run-PersonalEvidence.ps1" -Sesion resiliencia
docker compose --env-file platform/.env -f platform/compose-dev.yml --profile s07 up -d postgres-auth
& ".\scripts\Service-Personal.ps1" -Name auth -Action Start
& ".\scripts\Run-PersonalEvidence.ps1" -Sesion auth
& ".\scripts\Run-Legacy.ps1"
& ".\scripts\Run-PersonalEvidence.ps1" -Sesion s07
& ".\scripts\Run-PersonalEvidence.ps1" -Sesion s08
& ".\scripts\Run-PersonalEvidence.ps1" -Sesion s09
& ".\scripts\Run-PersonalEvidence.ps1" -Sesion s10
```

Ejecuta los comandos **uno después de otro**. S07 propio cambia temporalmente el issuer y lo restaura; no debe solaparse con pruebas que usen Keycloak. No copies `PS C:\...>`: ese texto es el indicador de PowerShell, no parte del comando.

## Abrir los servicios

| Vista | Dirección personal |
|---|---|
| Angular | <http://localhost:4200/?evidencia=1> |
| Gateway | <http://localhost:18080/actuator/health> |
| Eureka | <http://localhost:18761> |
| Keycloak | <http://localhost:18090> |
| Kafka UI | <http://localhost:18085> |
| Grafana | <http://localhost:13000/d/casa-o-nada-u2> |
| Prometheus | <http://localhost:19090> |

Las claves locales se conservan en `platform/.env` y `platform/runtime` y no se publican. Los pagos son simulaciones de desarrollo.

## Antes de entregar

Completar los siete campos editables del feedback en la última hoja de cada PDF. Las opiniones sobre la clase y la autoevaluación pertenecen a la estudiante. La guía pide aplicar actividades al Proyecto Sello: esta evidencia personal sobre Pagatu no sustituye la entrega grupal del dominio CASAS.
