# Reproducir la tarea personal

Ejecutar en PowerShell desde `C:\Users\grace\MicroServicios\Sesiòn 1\Pagatu`. Docker Desktop debe estar iniciado. Java 21, Maven y Node compatibles deben estar disponibles; Start-Dev prepara el Node portátil si hace falta.

## 1. Infraestructura y cuentas técnicas

```powershell
& ".\scripts\Start-Dev.ps1" -Observabilidad
node .\scripts\Verify-Personal.cjs setup
```

El bootstrap crea cuentas técnicas ADMIN y CLIENTE en el realm local y guarda sus secretos en `platform/runtime`, excluido de Git. No se usan como cuentas del frontend.

## 2. Ejecutar las prácticas en orden

```powershell
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

Esperar a que termine un comando antes de ejecutar el siguiente. S07 propio cambia temporalmente el issuer y lo restaura a Keycloak. Durante ese cambio, un JWT de Keycloak debe recibir 401: no se debe confundir con un error de Kafka o del proceso de pagos.

Los scripts validan que cada PID pertenezca a `java.exe` y al módulo personal antes de detenerlo. Las pruebas no borran volúmenes. S10 detiene y recupera únicamente PostgreSQL de cliente del proyecto personal.

## 3. Frontend y PKCE

Abrir <http://localhost:4200/?evidencia=1> e ingresar con una cuenta CLIENTE de desarrollo de este realm. Las credenciales existentes están en los archivos privados de `platform/runtime`; no forman parte del ZIP ni del repositorio público. También se puede registrar una cuenta desde Keycloak.

El modo de evidencia muestra la hora real y la cuenta. Pulsar Ingresar o Mi cuenta completa un nuevo Authorization Code y muestra el resultado de comprobar state y PKCE S256, sin revelar tokens ni code_verifier.

Para observar el estado pendiente real, detener pago con `Service-Personal.ps1 -Name pago -Action Stop`, crear una orden y volver a iniciarlo con `-Action Start`. Usar Yape/Plin para el camino feliz y Prueba de rechazo para CANCELADA. La pantalla consulta cada dos segundos y se detiene al obtener un resultado terminal.

El reporte `Verify-Frontend.cjs` documenta las órdenes 48 y 49 de esta ejecución. Si se repite desde otra base, los identificadores serán distintos: no se deben copiar como si fueran datos nuevos.

## 4. Compilación y Unidad 1

```powershell
& ".\scripts\Verify-Build.ps1"
& ".\scripts\Verify-Unit1.ps1"
python -m mkdocs build --strict -f MKDOCS/mkdocs.yml
```

La prueba de Unidad 1 arranca una segunda instancia temporal de catálogo en 8086 y la retira al terminar. Los reportes de esta entrega se guardan en `platform/runtime/evidencias-20261105`; la fecha y hora internas proceden de cada ejecución real.
