# S09 · Consistencia distribuida en procesos de negocio

**Estudiante:** Jannys Graciela Navarro Acrota  
**Grupo:** Casa o Nada  
**Repositorio:** [Stephmarc/Tareas-Distribuidas](https://github.com/Stephmarc/Tareas-Distribuidas)  
**Aporte:** evolución del servicio de órdenes y su integración con infraestructura y servicios separados.

!!! warning "Estado de verificación"
    Esta página documenta el código preparado. Las pruebas integrales con PostgreSQL, Kafka, Keycloak y Grafana todavía requieren ejecutar el entorno. No se presentan resultados esperados como si fueran pruebas ejecutadas. Las capturas se añadirán después de verificar cada caso.

## 1. Estados y compensación

```text
Consulta incompleta → CARRITO (total sin confirmar, sin evento de pago)
Consulta completa → PENDIENTE_PAGO
PENDIENTE_PAGO + pago.validado → PAGADA → orden.confirmada
PENDIENTE_PAGO + pago.fallido → CANCELADA → orden.cancelada
PAGADA o CANCELADA + evento repetido → sin cambio
```

La compensación implementada cancela la orden pendiente cuando el pago se rechaza. No se afirma que devuelva stock: esta versión no reserva inventario. Tampoco se afirma que cobre una pasarela real: `RECHAZADO` activa una condición determinista del procesador de práctica.

## 2. Idempotencia respaldada por la base

`pagos.orden_id` tiene una restricción UNIQUE. El consumidor usa `INSERT ... ON CONFLICT DO NOTHING` para que dos entregas concurrentes no generen dos pagos. El resultado y el evento de salida se registran en la misma transacción.

Órdenes cambia el estado mediante una actualización condicional: solo una orden en `PENDIENTE_PAGO` puede pasar a su estado final. Una repetición no vuelve a confirmar ni compensar, ni genera otro aviso. Cliente también usa una clave única por orden para evitar registrar el mismo aviso dos veces.

## 3. Pruebas integrales pendientes

1. Crear una orden normal y esperar PAGADA.
2. Reenviar el mismo evento y comprobar una sola fila en pagos.
3. Crear otra con método RECHAZADO y esperar CANCELADA.
4. Reenviar pago.fallido y comprobar que el estado final no vuelve a cambiar.
5. Revisar una sola fila de aviso y los logs de duplicado ignorado.

Capturar los datos anteriores y posteriores, junto con el mensaje concreto reenviado. Un test con mocks no demuestra la restricción UNIQUE en PostgreSQL.

## Error o hallazgo

La publicación con acuse no garantiza que el consumidor vea un evento una sola vez. Una caída después del acuse y antes de actualizar outbox repite el mensaje. La solución está en hacer que repetir el hecho no repita el efecto, con restricciones persistentes y transiciones condicionales.

## Reflexión técnica — borrador para defender

Una red puede perder la confirmación aunque la operación haya ocurrido. El emisor no distingue con certeza entre ese caso y una operación que nunca llegó. Reintentar evita perder trabajo, pero puede duplicarlo. Por eso la aplicación acepta reentregas y controla sus efectos. La restricción de la base resuelve también la carrera entre dos consumidores, que una comprobación previa en memoria no impediría.

## Evidencias y aporte verificable

Los archivos citados corresponden a la implementación del repositorio. Las capturas de ejecución se incorporarán aquí después de la prueba. El [registro de verificación](../verificacion.md) distingue resultados confirmados de pendientes.

## Guía consultada

[Sesión del docente](https://262dist.github.io/pagatu/sesiones/S09_Consistencia_Distribuida_Procesos_Negocio/). La guía se utiliza como referencia técnica; sus instrucciones de entrega no sustituyen la solicitud de la estudiante.

[Feedback personal pendiente](../feedback.md).
