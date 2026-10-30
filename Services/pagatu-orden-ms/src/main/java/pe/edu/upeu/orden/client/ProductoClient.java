package pe.edu.upeu.orden.client;

import java.math.BigDecimal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "pagatu-catalogo-ms")
public interface ProductoClient {
  @GetMapping("/api/v1/productos/{id}")
  Producto findById(@PathVariable("id") Long id);

  record Producto(Long id, String nombre, BigDecimal precio) {}
}
