package pe.edu.upeu.cliente.event;

import jakarta.persistence.*;

@Entity
@Table(name = "outbox")
public class Outbox {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Column(nullable = false, unique = true)
  public String eventKey;

  public String topic;
  public String messageKey;

  @Column(columnDefinition = "TEXT")
  public String payload;

  public boolean enviado;
}
