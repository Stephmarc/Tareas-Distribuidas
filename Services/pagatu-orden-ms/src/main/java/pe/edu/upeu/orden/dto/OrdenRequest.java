package pe.edu.upeu.orden.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrdenRequest {

    @NotBlank
    @Size(max = 100)
    private String nombre;

    @Size(max = 255)
    private String descripcion;

    @NotNull
    private Long clienteId;

    @NotNull
    @Size(max = 30)
    private String estado;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal total;
}