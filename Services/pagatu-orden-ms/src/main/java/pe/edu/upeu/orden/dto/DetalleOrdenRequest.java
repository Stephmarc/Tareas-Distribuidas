package pe.edu.upeu.orden.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetalleOrdenRequest {

  @NotNull
  private Long productoId;

  @NotNull
  @Positive
  private Integer cantidad;

  @NotNull
  @DecimalMin(value = "0.0", inclusive = true)
  private BigDecimal precioUnitario;

  @NotNull
  private Long ordenId;
}
