package pe.edu.upeu.cliente.event;

import jakarta.persistence.EntityManager;
import org.slf4j.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Component
public class AvisoListener {

  private final EntityManager em;
  private final ObjectMapper json;
  private final Logger log = LoggerFactory.getLogger(getClass());

  public AvisoListener(EntityManager em, ObjectMapper json) {
    this.em = em;
    this.json = json;
  }

  @KafkaListener(topics = "cliente-eventos", groupId = "pagatu-cliente-avisos")
  @Transactional
  public void recibir(String payload) {
    Evento e = json.readValue(payload, Evento.class);
    if (
      !java.util.Set.of("orden.confirmada", "orden.cancelada").contains(
        e.tipoEvento()
      )
    ) return;
    int n = em
      .createNativeQuery(
        "INSERT INTO avisos(orden_id,cliente_id,estado) VALUES (:id,:cliente,:estado) ON CONFLICT(orden_id) DO NOTHING"
      )
      .setParameter("id", e.ordenId())
      .setParameter("cliente", e.clienteId())
      .setParameter("estado", e.tipoEvento())
      .executeUpdate();
    log.info(
      "Aviso registrado orden={} evento={} resultado={} traceId={}",
      e.ordenId(),
      e.tipoEvento(),
      n == 1 ? "aplicado" : "duplicado ignorado",
      e.traceId()
    );
  }
}
