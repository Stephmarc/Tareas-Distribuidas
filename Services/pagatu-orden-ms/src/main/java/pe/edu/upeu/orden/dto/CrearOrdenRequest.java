package pe.edu.upeu.orden.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record CrearOrdenRequest(
  @NotBlank String metodoPago,
  @NotEmpty List<@Valid Item> detalles
) {
  public record Item(
    @NotNull @Positive Long idProducto,
    @NotNull @Positive Integer cantidad
  ) {}
}
