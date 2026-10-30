# S10 · Observabilidad y diagnóstico

**Estudiante:** Jannys Graciela Navarro Acrota  
**Grupo:** Casa o Nada  
**Repositorio:** [Stephmarc/Tareas-Distribuidas](https://github.com/Stephmarc/Tareas-Distribuidas)  
**Aporte:** evolución del servicio de órdenes y su integración con infraestructura y servicios separados.

!!! warning "Estado de verificación"
    Esta página documenta el código preparado. Las pruebas integrales con PostgreSQL, Kafka, Keycloak y Grafana todavía requieren ejecutar el entorno. No se presentan resultados esperados como si fueran pruebas ejecutadas. Las capturas se añadirán después de verificar cada caso.

## 1. Liveness y readiness

Los servicios configuran `/actuator/health/liveness` con el estado del proceso y `/actuator/health/readiness` con la disponibilidad y la base de datos. Detener PostgreSQL no significa necesariamente que la JVM haya muerto. El proceso puede seguir vivo y, al mismo tiempo, no estar preparado para atender una operación persistente.

## 2. Métricas y logs

Micrometer expone Prometheus. `obs/prometheus.yml` consulta los puertos de los servicios en el host y el endpoint de métricas de Keycloak. Se utilizan targets explícitos para que la configuración sea pequeña y verificable; no se afirma que se haya implementado descubrimiento automático de métricas.

Promtail monta los directorios reales de logs de catálogo, órdenes, pagos, cliente y Keycloak. Sus etiquetas permiten consultar el origen en Loki. El tablero `obs/dashboards/unidad2.json` provisiona disponibilidad, tasa de peticiones por estado y logs.

## 3. Correlación

Cada filtro HTTP conserva o genera `X-Trace-ID` y lo coloca en MDC. El interceptor Feign lo envía al catálogo junto al JWT verificado. Los eventos incluyen `traceId`; los consumidores lo restauran o lo registran explícitamente. MDC por sí mismo no transporta información entre procesos ni entre hilos.

## 4. Diagnóstico de falla — por ejecutar

| Momento | Acción | Dato que debe registrarse |
| --- | --- | --- |
| Antes | Abrir tablero | Hora real, disponibilidad y petición válida |
| Durante | Detener catálogo | up del target, error/fallback, traceId |
| Durante | Crear orden | CARRITO y total null |
| Después | Recuperar catálogo | up recuperado y resultado de HALF_OPEN |

Los valores aún no se han completado porque no existe una ejecución integral confirmada. No se inventan latencias ni cifras de errores.

## Error o hallazgo

Una etiqueta de Promtail no basta para recolectar un archivo: el directorio también debe existir y estar montado dentro del contenedor. El Compose conecta cada etiqueta con su volumen correspondiente. Los servicios deben haberse iniciado y haber generado sus logs antes de comprobar el tablero.

## Reflexión técnica — borrador para defender

Estar vivo no equivale a poder atender una solicitud. Una dependencia caída puede impedir persistir aunque el proceso siga respondiendo. Las métricas detectan el cambio y los logs ayudan a explicar su causa. La correlación permite seguir una misma operación por varios servicios. La prueba de falla debe confirmar estas señales con datos reales; un tablero vacío solo demuestra que existe una configuración visual.

## Evidencias y aporte verificable

Los archivos citados corresponden a la implementación del repositorio. Las capturas de ejecución se incorporarán aquí después de la prueba. El [registro de verificación](../verificacion.md) distingue resultados confirmados de pendientes.

## Guía consultada

[Sesión del docente](https://262dist.github.io/pagatu/sesiones/S10_Observabilidad_Diagnostico_Sistemas_Distribuidos/). La guía se utiliza como referencia técnica; sus instrucciones de entrega no sustituyen la solicitud de la estudiante.

[Feedback personal pendiente](../feedback.md).
