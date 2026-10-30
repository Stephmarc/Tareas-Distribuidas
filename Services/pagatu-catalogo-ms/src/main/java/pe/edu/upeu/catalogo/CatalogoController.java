package pe.edu.upeu.catalogo;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upeu.catalogo.dto.ProductoResponse;

@RestController
@RequestMapping("/api/v1/productos")
public class CatalogoController {

  private final ProductoRepository r;

  public CatalogoController(ProductoRepository r) {
    this.r = r;
  }

  private ProductoResponse dto(pe.edu.upeu.catalogo.entity.Producto p) {
    return new ProductoResponse(p.id, p.nombre, p.precio);
  }

  @GetMapping
  public List<ProductoResponse> listar() {
    return r.findAll().stream().map(this::dto).toList();
  }

  @GetMapping("/{id}")
  public ProductoResponse obtener(@PathVariable Long id) {
    return dto(
      r
        .findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
    );
  }
}
