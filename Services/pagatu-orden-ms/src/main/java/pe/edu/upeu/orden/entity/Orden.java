package pe.edu.upeu.orden.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "ordenes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Orden {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "nombre", nullable = false, length = 100)
  private String nombre;

  @Column(name = "descripcion", length = 255)
  private String descripcion;

  @Column(name = "cliente_id")
  private String clienteId;

  @Column(name = "estado", nullable = false, length = 30)
  private String estado;

  @Column(name = "total", precision = 10, scale = 2)
  private BigDecimal total;

  @OneToMany(
    mappedBy = "orden",
    fetch = FetchType.LAZY,
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  @Builder.Default
  private List<DetalleOrden> detalles = new ArrayList<>();
}
