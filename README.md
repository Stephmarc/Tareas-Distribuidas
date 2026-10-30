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