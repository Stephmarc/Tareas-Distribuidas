package pe.edu.upeu.orden.event;

import java.util.concurrent.TimeUnit;
import org.slf4j.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Service
@EnableScheduling
public class OutboxService {

  private final OutboxRepository r;
  private final KafkaTemplate<String, String> kafka;
  private final ObjectMapper json;
  private final Logger log = LoggerFactory.getLogger(getClass());

  public OutboxService(
    OutboxRepository r,
    KafkaTemplate<String, String> kafka,
    ObjectMapper json
  ) {
    this.r = r;
    this.kafka = kafka;
    this.json = json;
  }

  @Transactional
  public void registrar(String topic, Evento evento) {
    Outbox o = new Outbox();
    o.eventKey = evento.tipoEvento() + ":" + evento.ordenId();
    o.topic = topic;
    o.messageKey = evento.ordenId().toString();
    o.payload = json.writeValueAsString(evento);
    r.save(o);
  }

  @Scheduled(fixedDelay = 2000)
  @Transactional
  public void publicar() {
    for (Outbox o : r.findTop20ByEnviadoFalseOrderByIdAsc()) {
      try {
        kafka.send(o.topic, o.messageKey, o.payload).get(5, TimeUnit.SECONDS);
        o.enviado = true;
        r.save(o);
        log.info(
          "Evento publicado topic={} key={} payload={}",
          o.topic,
          o.messageKey,
          o.payload
        );
      } catch (Exception ex) {
        log.warn(
          "Outbox pendiente key={} motivo={}",
          o.eventKey,
          ex.toString()
        );
        break;
      }
    }
  }
}
