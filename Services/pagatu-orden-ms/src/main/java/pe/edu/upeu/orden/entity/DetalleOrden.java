package pe.edu.upeu.orden.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "detalles_orden")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleOrden {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "producto_id", nullable = false)
  private Long productoId;

  @Column(name = "cantidad", nullable = false)
  private Integer cantidad;

  @Column(name = "precio_unitario", precision = 10, scale = 2)
  private BigDecimal precioUnitario;

  private String nombreProducto;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "orden_id", nullable = false)
  private Orden orden;
}
