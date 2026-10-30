# S11 · Integración del cliente Angular con Keycloak

**Estudiante:** Jannys Graciela Navarro Acrota  
**Grupo:** Casa o Nada  
**Repositorio:** [Stephmarc/Tareas-Distribuidas](https://github.com/Stephmarc/Tareas-Distribuidas)  
**Aporte:** evolución del servicio de órdenes y su integración con infraestructura y servicios separados.

!!! warning "Estado de verificación"
    Esta página documenta el código preparado. Las pruebas integrales con PostgreSQL, Kafka, Keycloak y Grafana todavía requieren ejecutar el entorno. No se presentan resultados esperados como si fueran pruebas ejecutadas. Las capturas se añadirán después de verificar cada caso.

## 1. Cliente e identidad

`clients/pagatu-frontend` utiliza Angular 22, el Gateway en 18080 y Keycloak en 18090. El adaptador oficial `keycloak-js` gestiona Authorization Code con PKCE S256. El cliente es público y no contiene client secret. Direct Access Grants permanece deshabilitado.

`clienteId` es literalmente el `sub` verificado. En este caso identifica al propietario de la orden y no requiere un atributo adicional mantenido manualmente. El JWT se conserva en memoria del adaptador; no se guarda en localStorage.

## 2. Flujo de la interfaz

Tras autenticarse, la persona consulta productos y registra una orden. El formulario envía producto, cantidad y método; omite precio, total e identidad. Mientras hay una solicitud de creación en curso se deshabilita el botón.

El listado distingue `CARRITO`, `PENDIENTE_PAGO`, `PAGADA` y `CANCELADA`. Un total null se presenta como «Por confirmar», nunca como cero. Una respuesta 201 significa que existe la orden, no que el pago haya concluido.

## 3. Consistencia eventual

Si existe una orden pendiente, la interfaz consulta cada dos segundos. Detiene el polling al llegar a un estado final, al destruirse el componente o después de 120 intentos. Si vence ese límite, conserva la información pendiente y permite una consulta manual. Evita consultas simultáneas del mismo temporizador.

## 4. Pruebas pendientes

Capturar el login real y verificar state y PKCE en las herramientas de red sin publicar tokens. Crear una orden normal y otra con RECHAZADO; observar ambos estados finales sin recargar. Probar también catálogo detenido para el carrito. Documentar la transición con su hora real.

La interfaz preparada contiene catálogo de productos y mis pedidos. El recorrido didáctico intermedio de S11 con CRUD completo de categorías y sin seguridad todavía no se ha reproducido ni evidenciado; no se considera completado por mostrar el cliente final.

## Error o hallazgo

El Node instalado inicialmente (24.11.0) es inferior al mínimo exigido por Angular 22.2. Se preparó un runtime portátil 24.22.2 para verificar el build, sin modificar la instalación del sistema. El script de arranque verifica la versión y permite obtener ese runtime localmente.

## Reflexión técnica — borrador para defender

La interfaz debe representar lo que el sistema ya sabe. Una confirmación HTTP no resuelve un pago que aún está en una cola. Mostrarlo como pagado induciría una decisión basada en información falsa. El polling es sencillo y suficiente para esta práctica de baja frecuencia. La autorización sigue estando en el servidor: filtrar una tabla en el navegador no protege las órdenes de otra persona.

## Evidencias y aporte verificable

Los archivos citados corresponden a la implementación del repositorio. Las capturas de ejecución se incorporarán aquí después de la prueba. El [registro de verificación](../verificacion.md) distingue resultados confirmados de pendientes.

## Guía consultada

[Sesión del docente](https://262dist.github.io/pagatu/sesiones/S11_Integracion_Cliente_Frontend_Angular_Keycloak/). La guía se utiliza como referencia técnica; sus instrucciones de entrega no sustituyen la solicitud de la estudiante.

[Feedback personal pendiente](../feedback.md).
