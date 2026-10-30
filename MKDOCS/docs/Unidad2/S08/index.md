# S08 · Mensajería asíncrona entre servicios

**Estudiante:** Jannys Graciela Navarro Acrota  
**Grupo:** Casa o Nada  
**Repositorio:** [Stephmarc/Tareas-Distribuidas](https://github.com/Stephmarc/Tareas-Distribuidas)  
**Aporte:** evolución del servicio de órdenes y su integración con infraestructura y servicios separados.

!!! warning "Estado de verificación"
    Esta página documenta el código preparado. Las pruebas integrales con PostgreSQL, Kafka, Keycloak y Grafana todavía requieren ejecutar el entorno. No se presentan resultados esperados como si fueran pruebas ejecutadas. Las capturas se añadirán después de verificar cada caso.

## 1. Servicios y topics

Órdenes registra una operación; pagos confirma o rechaza el pago de práctica; cliente registra un aviso interno. Kafka desacopla el momento de publicación del momento de consumo. Se usa el identificador de orden como key, para conservar la asociación del proceso dentro de una partición.

| Topic | Hecho | Productor | Grupo consumidor |
| --- | --- | --- | --- |
| orden-eventos | orden.creada | órdenes | pagatu-pago |
| pago-eventos | pago.validado / pago.fallido | pagos | pagatu-orden |
| cliente-eventos | orden.confirmada / orden.cancelada | órdenes | pagatu-cliente-avisos |

El último flujo es el evento adicional de esta implementación. Registra un aviso en la base de cliente; no envía mensajes a WhatsApp ni a terceros.

## 2. Contrato

`Evento` contiene `tipoEvento: String`, `ordenId: Long`, `clienteId: String`, `total: BigDecimal`, `metodoPago: String` y `traceId: String`. El importe y método permiten procesar el pago sin consultar de vuelta a órdenes. Se transmite JSON y se define un DTO local por servicio.

## 3. Publicación persistente

La misma transacción guarda la orden y una fila en `outbox`. Un publicador periódico envía la fila y la marca como enviada después del acuse de Kafka. Si el broker falla, el evento permanece pendiente. Si la aplicación cae después de publicar pero antes de marcar, puede repetir el envío: por eso los consumidores deben ser idempotentes.

## 4. Prueba de desacople pendiente

Detener pagos, crear una orden y comprobar que permanece pendiente. Revisar el mensaje y el lag en Kafka UI. Volver a iniciar pagos y observar su procesamiento. Repetir con cliente detenido para el evento adicional. Capturar publicación, mensaje, lag y consumo, sin sustituirlos por una explicación teórica.

## Error o hallazgo

Guardar una orden y publicar directamente después deja una ventana en la que la aplicación podría caer y perder el evento. La tabla outbox evita depender de que ambos pasos externos concluyan en una sola llamada. El mecanismo ofrece entrega al menos una vez y exige idempotencia.

## Reflexión técnica — borrador para defender

Un evento útil contiene los datos necesarios del hecho ocurrido. Si solo llevara el identificador, pagos dependería de una consulta síncrona a órdenes para conocer el importe. Eso volvería a exigir disponibilidad simultánea. Copiar esos datos en el evento conserva el desacople temporal. El consumidor guarda su resultado en su propia base y publica otro hecho, sin compartir tablas.

## Evidencias y aporte verificable

Los archivos citados corresponden a la implementación del repositorio. Las capturas de ejecución se incorporarán aquí después de la prueba. El [registro de verificación](../verificacion.md) distingue resultados confirmados de pendientes.

## Guía consultada

[Sesión del docente](https://262dist.github.io/pagatu/sesiones/S08_Mensajeria_Asincrona_Entre_Servicios/). La guía se utiliza como referencia técnica; sus instrucciones de entrega no sustituyen la solicitud de la estudiante.

[Feedback personal pendiente](../feedback.md).
