package pe.edu.upeu.orden.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private Long clienteId;
    private String estado;
    private BigDecimal total;
    private List<DetalleOrdenResponse> detalles;
}