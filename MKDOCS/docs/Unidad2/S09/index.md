# S09 · Consistencia y compensación

**Jannys Graciela Navarro Acrota** · Tarea personal de Sistemas Distribuidos.

Grupo de referencia: Casa o Nada. Esta documentación corresponde al avance individual, no al informe grupal oficial.

## Trabajo realizado

Se implementó el flujo de confirmación o cancelación de una orden y la protección contra mensajes repetidos.

## Resultados

| Orden | Pago | Estado final |
| --- | --- | --- |
| 1 | Validado | PAGADA |
| 2 | Rechazado en la simulación | CANCELADA |

Se reenviaron **orden.creada**, **pago.fallido** y **orden.cancelada**. Los consumidores los ignoraron como duplicados. PostgreSQL conservó **una fila por orden** en pagos y avisos.

[Estados comprobados](../evidencias/estados-pago.json) · [Logs de duplicados](../evidencias/duplicados.txt).

## Explicación breve

Kafka puede entregar un mensaje más de una vez. La restricción UNIQUE y las transiciones condicionales evitan repetir el efecto. Si el pago falla, la compensación deja la orden cancelada. Los pagos de esta práctica son simulados.
