package pe.edu.upeu.orden.dto;

import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleOrdenResponse {

  private Long id;
  private String nombreProducto;
  private Long productoId;
  private Integer cantidad;
  private BigDecimal precioUnitario;
  private Long ordenId;
}
