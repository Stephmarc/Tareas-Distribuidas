package pe.edu.upeu.orden.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.upeu.orden.client.ProductoClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoConsultaService {

  private final ProductoClient cliente;

  @CircuitBreaker(name = "catalogo", fallbackMethod = "fallback")
  public ProductoClient.Producto consultar(Long id) {
    return cliente.findById(id);
  }

  public ProductoClient.Producto fallback(Long id, Throwable ex) {
    log.warn("Fallback activado producto={} motivo={}", id, ex.toString());
    return null;
  }
}
