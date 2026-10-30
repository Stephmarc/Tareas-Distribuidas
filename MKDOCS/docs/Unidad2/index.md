# Unidad 2 · Sistema distribuido robusto

**Estudiante:** Jannys Graciela Navarro Acrota  
**Grupo:** Casa o Nada  
**Repositorio:** [Stephmarc/Tareas-Distribuidas](https://github.com/Stephmarc/Tareas-Distribuidas)  
**Aporte:** evolución del servicio de órdenes y su integración con infraestructura y servicios separados.

!!! warning "Estado de verificación"
    Esta página documenta el código preparado. Las pruebas integrales con PostgreSQL, Kafka, Keycloak y Grafana todavía requieren ejecutar el entorno. No se presentan resultados esperados como si fueran pruebas ejecutadas. Las capturas se añadirán después de verificar cada caso.

## Organización

Esta unidad reúne las sesiones S06 a S11 y la transición de S07 a Keycloak. Se conserva el trabajo previo de órdenes de este repositorio; el nombre del grupo no se utiliza para inventar otro dominio de negocio.

| Sesión | Implementación preparada | Evidencia integral |
| --- | --- | --- |
| [S06 · Comunicación síncrona resiliente](S06/index.md) | Código y configuración | Pendiente de ejecución |
| [S07 · Seguridad distribuida y control de acceso](S07/index.md) | Código y configuración | Pendiente de ejecución |
| [S08 · Mensajería asíncrona entre servicios](S08/index.md) | Código y configuración | Pendiente de ejecución |
| [S09 · Consistencia distribuida en procesos de negocio](S09/index.md) | Código y configuración | Pendiente de ejecución |
| [S10 · Observabilidad y diagnóstico](S10/index.md) | Código y configuración | Pendiente de ejecución |
| [S11 · Integración del cliente Angular con Keycloak](S11/index.md) | Código y configuración | Pendiente de ejecución |

## Arquitectura

Navegador Angular → Gateway → servicios con JWT. Órdenes consulta precios por Feign/Eureka. Órdenes → Kafka → pagos → Kafka → órdenes. El resultado genera un evento adicional hacia cliente. Cada servicio persistente posee su base. Config Server centraliza los valores de ambiente. Grafana combina métricas y logs.

## Arranque

Desde PowerShell en la raíz del repositorio:

```powershell
.\scripts\Start-Dev.ps1 -Observabilidad
```

Docker Desktop debe estar iniciado. El script no elimina volúmenes. Ejecuta las pruebas Java antes de arrancar servicios. Las cuentas personales se crean desde Keycloak. Si un puerto está ocupado, se detiene con un mensaje en vez de terminar procesos ajenos.

Consulta el [registro de verificación](verificacion.md), la [auditoría de unidad 1](../Unidad1/auditoria.md) y el [feedback personal](feedback.md).
