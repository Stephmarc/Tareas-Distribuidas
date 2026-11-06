# Sistemas Distribuidos - tarea personal

Jannys Graciela Navarro Acrota. Grupo de referencia: Casa o Nada.

Esta es la evidencia individual de las sesiones 6 a 11. El informe grupal oficial se elaborara aparte.

La documentacion esta en MKDOCS/docs/Unidad2. Incluye pruebas reales de Feign, Circuit Breaker, JWT/Keycloak, Kafka, compensacion, duplicados, Grafana y Angular.

## Ejecutar el proyecto

Con Docker Desktop abierto, desde PowerShell en esta carpeta:

```powershell
.\scripts\Start-Dev.ps1 -Observabilidad
```

Se requieren Java 21, Maven y Node compatible con Angular 22. El script prepara un runtime portatil cuando corresponde. Las credenciales locales de desarrollo quedan en platform/.env y platform/runtime, fuera de Git.

## Documentacion

```text
python -m pip install -r requirements-docs.txt
python -m mkdocs build --strict -f MKDOCS/mkdocs.yml
python -m mkdocs serve -f MKDOCS/mkdocs.yml
```

El workflow publica el sitio al hacer push a main. Los pagos y las cuentas utilizados en las pruebas son de desarrollo.
## Evidencias verificadas el 5/11/2026

Los seis informes PDF se descargan desde MKDOCS/docs/Unidad2/S06 a S11. Cada sesión conserva requests, respuestas y transcripts originales. Los anexos de feedback contienen campos editables que completa Jannys.

Ejecuta las pruebas secuencialmente. Run-Legacy.ps1 prueba el issuer propio de S07 y restaura Keycloak; no lo solapes con Verify-Personal.cjs. El modo Angular ?evidencia=1 muestra hora y cuenta reales y comprueba PKCE sin registrar secretos.

La revisión de Unidad 1 incorpora una prueba real de dos instancias en scripts/Verify-Unit1.ps1. La defensa oral y la evaluación del docente siguen siendo presenciales.

## Capturas completadas el 6/11/2026

Se incorporaron quince capturas complementarias de código y resultados originales en VS Code, distribuidas entre S06 y S11. Las imágenes conservan reloj y usuario visibles; las fechas internas de las ejecuciones del 5 de noviembre no se modificaron. Los seis PDF fueron actualizados.
