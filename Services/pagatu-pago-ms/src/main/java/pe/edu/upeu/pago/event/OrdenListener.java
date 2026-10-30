package pe.edu.upeu.pago.event;

import jakarta.persistence.EntityManager;
import org.slf4j.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Component
public class OrdenListener {

  private final ObjectMapper json;
  private final EntityManager em;
  private final OutboxService outbox;
  private final Logger log = LoggerFactory.getLogger(getClass());

  public OrdenListener(
    ObjectMapper json,
    EntityManager em,
    OutboxService outbox
  ) {
    this.json = json;
    this.em = em;
    this.outbox = outbox;
  }

  @KafkaListener(topics = "orden-eventos", groupId = "pagatu-pago")
  @Transactional
  public void recibir(String payload) {
    Evento e = json.readValue(payload, Evento.class);
    if (!"orden.creada".equals(e.tipoEvento())) return;
    if (
      e.total() == null || e.total().signum() < 0
    ) throw new IllegalArgumentException("Importe inválido");
    String estado = "RECHAZADO".equals(e.metodoPago())
      ? "RECHAZADO"
      : "VALIDADO";
    try {
      if (e.traceId() != null) MDC.put("traceId", e.traceId());
      int n = em
        .createNativeQuery(
          "INSERT INTO pagos(orden_id,cliente_id,total,estado) VALUES (:id,:cliente,:total,:estado) ON CONFLICT (orden_id) DO NOTHING"
        )
        .setParameter("id", e.ordenId())
        .setParameter("cliente", e.clienteId())
        .setParameter("total", e.total())
        .setParameter("estado", estado)
        .executeUpdate();
      if (n == 0) {
        log.info("Pago duplicado ignorado orden={}", e.ordenId());
        return;
      }
      outbox.registrar(
        "pago-eventos",
        new Evento(
          estado.equals("VALIDADO") ? "pago.validado" : "pago.fallido",
          e.ordenId(),
          e.clienteId(),
          e.total(),
          e.metodoPago(),
          e.traceId()
        )
      );
      log.info("Pago orden={} estado={}", e.ordenId(), estado);
    } finally {
      MDC.remove("traceId");
    }
  }
}
