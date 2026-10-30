package pe.edu.upeu.orden.event;

import jakarta.persistence.EntityManager;
import org.slf4j.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Component
public class PagoListener {

  private final ObjectMapper json;
  private final EntityManager em;
  private final OutboxService outbox;
  private final Logger log = LoggerFactory.getLogger(getClass());

  public PagoListener(
    ObjectMapper json,
    EntityManager em,
    OutboxService outbox
  ) {
    this.json = json;
    this.em = em;
    this.outbox = outbox;
  }

  @KafkaListener(topics = "pago-eventos", groupId = "pagatu-orden")
  @Transactional
  public void recibir(String payload) {
    Evento e = json.readValue(payload, Evento.class);
    String estado = switch (e.tipoEvento()) {
      case "pago.validado" -> "PAGADA";
      case "pago.fallido" -> "CANCELADA";
      default -> null;
    };
    if (estado == null) return;
    try {
      if (e.traceId() != null) MDC.put("traceId", e.traceId());
      int n = em
        .createQuery(
          "update Orden o set o.estado=:estado where o.id=:id and o.estado='PENDIENTE_PAGO'"
        )
        .setParameter("estado", estado)
        .setParameter("id", e.ordenId())
        .executeUpdate();
      log.info(
        "Saga orden={} estado={} resultado={}",
        e.ordenId(),
        estado,
        n == 1 ? "aplicado" : "duplicado ignorado"
      );
      if (n == 1) outbox.registrar(
        "cliente-eventos",
        new Evento(
          estado.equals("PAGADA") ? "orden.confirmada" : "orden.cancelada",
          e.ordenId(),
          e.clienteId(),
          e.total(),
          e.metodoPago(),
          e.traceId()
        )
      );
    } finally {
      MDC.remove("traceId");
    }
  }
}
