package pe.edu.upeu.cliente.event;

import java.math.BigDecimal;

public record Evento(
  String tipoEvento,
  Long ordenId,
  String clienteId,
  BigDecimal total,
  String metodoPago,
  String traceId
) {}
