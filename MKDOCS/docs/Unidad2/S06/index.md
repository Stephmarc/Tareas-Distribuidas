# S06 · Comunicación síncrona resiliente

**Estudiante:** Jannys Graciela Navarro Acrota  
**Grupo:** Casa o Nada  
**Repositorio:** [Stephmarc/Tareas-Distribuidas](https://github.com/Stephmarc/Tareas-Distribuidas)  
**Aporte:** evolución del servicio de órdenes y su integración con infraestructura y servicios separados.

!!! warning "Estado de verificación"
    Esta página documenta el código preparado. Las pruebas integrales con PostgreSQL, Kafka, Keycloak y Grafana todavía requieren ejecutar el entorno. No se presentan resultados esperados como si fueran pruebas ejecutadas. Las capturas se añadirán después de verificar cada caso.

## 1. Orden con precio confirmado

El endpoint de creación recibe únicamente el método de pago y los identificadores y cantidades de los productos. El cliente no puede fijar el precio ni el total. El servicio consulta el catálogo con `ProductoClient`, cuyo nombre lógico es `pagatu-catalogo-ms`; Eureka resuelve la instancia.

`RegistroOrdenService` guarda el nombre y precio obtenidos en el detalle. La copia es histórica: una modificación posterior del catálogo no debe cambiar una venta anterior. Se mantiene el nombre de las tablas existentes, `ordenes` y `detalles_orden`, en lugar de mezclar el esquema del ejemplo con el de este repositorio.

## 2. Circuit Breaker

`ProductoConsultaService` es un bean separado. La llamada atraviesa el proxy de Spring y la anotación de Resilience4j puede interceptarla. La ventana considera cinco llamadas; con al menos cinco muestras y 50 % de fallos, el circuito puede abrirse. Tras diez segundos permite dos llamadas de prueba.

Si no se confirma algún producto, la orden conserva `CARRITO`, con `total: null`. Una suma parcial nunca se presenta como importe final. No se publica un evento de pago para ese carrito.

## 3. Cliente independiente

`pagatu-cliente-ms` cuenta con su propio esquema PostgreSQL, migración Flyway, DTO, repositorio, servicio, controlador y configuración externa. El acceso por Gateway y directo exige el rol ADMIN para su CRUD administrativo. No existe una llave foránea entre las bases de catálogo, cliente y órdenes.

## 4. Prueba reproducible

Con sesión válida en el frontend, crear una orden con un producto existente. Detener únicamente catálogo y repetir. Consultar el estado del circuito con un token ADMIN:

```powershell
Invoke-RestMethod http://localhost:8082/actuator/circuitbreakers -Headers $headers
Invoke-RestMethod http://localhost:8082/actuator/circuitbreakerevents -Headers $headers
```

Capturar por separado el caso correcto, el carrito sin precio y los estados CLOSED, OPEN y HALF_OPEN. Los comandos anteriores son instrucciones de reproducción, no evidencia de ejecución.

## Error o hallazgo

La dependencia de Eureka estaba originalmente en `dependencyManagement`, que administra versiones pero no incorpora la biblioteca. Se movió a las dependencias efectivas del servicio. También se preservó V1: los cambios de identidad y nulabilidad se aplican en V2, sin reiniciar datos.

## Reflexión técnica — borrador para defender

Un timeout limita cuánto espera una llamada. Un Circuit Breaker observa fallos repetidos y deja temporalmente de llamar a una dependencia que no responde. Por eso reduce trabajo inútil y permite una respuesta de negocio controlada. Aquí esa respuesta conserva un carrito sin inventar precios. HALF_OPEN permite comprobar la recuperación con pocas llamadas. Aumentar el timeout no proporciona esa protección ni distingue el estado de recuperación.

## Evidencias y aporte verificable

Los archivos citados corresponden a la implementación del repositorio. Las capturas de ejecución se incorporarán aquí después de la prueba. El [registro de verificación](../verificacion.md) distingue resultados confirmados de pendientes.

## Guía consultada

[Sesión del docente](https://262dist.github.io/pagatu/sesiones/S06_Comunicacion_Sincrona_Resiliente/). La guía se utiliza como referencia técnica; sus instrucciones de entrega no sustituyen la solicitud de la estudiante.

[Feedback personal pendiente](../feedback.md).
