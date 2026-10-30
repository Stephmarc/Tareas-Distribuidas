package pe.edu.upeu.orden.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenResponse {

  private Long id;
  private String nombre;
  private String descripcion;
  private String clienteId;
  private String estado;
  private BigDecimal total;
  private List<DetalleOrdenResponse> detalles;
}
