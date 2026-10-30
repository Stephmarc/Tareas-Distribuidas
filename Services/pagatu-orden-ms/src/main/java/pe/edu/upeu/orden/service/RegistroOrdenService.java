package pe.edu.upeu.orden.service;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.orden.dto.*;
import pe.edu.upeu.orden.entity.*;
import pe.edu.upeu.orden.mapper.OrdenMapper;
import pe.edu.upeu.orden.repository.OrdenRepository;

@Service
@RequiredArgsConstructor
public class RegistroOrdenService {

  private final OrdenRepository repository;
  private final OrdenMapper mapper;
  private final pe.edu.upeu.orden.event.OutboxService outbox;
  private final ProductoConsultaService productos;

  @Transactional
  public OrdenResponse crear(CrearOrdenRequest request, String sub) {
    Orden orden = Orden.builder()
      .nombre("Orden de " + sub)
      .clienteId(sub)
      .estado("CARRITO")
      .build();
    BigDecimal total = BigDecimal.ZERO;
    boolean completa = true;
    for (var item : request.detalles()) {
      var producto = productos.consultar(item.idProducto());
      var detalle = DetalleOrden.builder()
        .orden(orden)
        .productoId(item.idProducto())
        .cantidad(item.cantidad())
        .build();
      if (producto == null) {
        completa = false;
      } else {
        detalle.setPrecioUnitario(producto.precio());
        detalle.setNombreProducto(producto.nombre());
        total = total.add(
          producto.precio().multiply(BigDecimal.valueOf(item.cantidad()))
        );
      }
      orden.getDetalles().add(detalle);
    }
    orden.setTotal(completa ? total : null);
    orden.setEstado(completa ? "PENDIENTE_PAGO" : "CARRITO");
    Orden guardada = repository.save(orden);
    if (completa) outbox.registrar(
      "orden-eventos",
      new pe.edu.upeu.orden.event.Evento(
        "orden.creada",
        guardada.getId(),
        sub,
        total,
        request.metodoPago(),
        org.slf4j.MDC.get("traceId")
      )
    );
    return mapper.toResponse(guardada);
  }
}
