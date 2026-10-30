package pe.edu.upeu.pago.event;

import java.math.BigDecimal;

public record Evento(
  String tipoEvento,
  Long ordenId,
  String clienteId,
  BigDecimal total,
  String metodoPago,
  String traceId
) {}
