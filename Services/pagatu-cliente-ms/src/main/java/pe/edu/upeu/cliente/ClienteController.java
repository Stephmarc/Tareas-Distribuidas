package pe.edu.upeu.cliente;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

  public record Datos(@NotBlank String nombre, String documento) {}

  public record Respuesta(Long id, String nombre, String documento) {}

  private final ClienteService s;

  public ClienteController(ClienteService s) {
    this.s = s;
  }

  private Respuesta dto(Cliente c) {
    return new Respuesta(c.id, c.nombre, c.documento);
  }

  @GetMapping
  public List<Respuesta> listar() {
    return s.listar().stream().map(this::dto).toList();
  }

  @GetMapping("/{id}")
  public Respuesta obtener(@PathVariable Long id) {
    return dto(s.obtener(id));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Respuesta crear(@Valid @RequestBody Datos d) {
    return dto(s.guardar(null, d));
  }

  @PutMapping("/{id}")
  public Respuesta actualizar(
    @PathVariable Long id,
    @Valid @RequestBody Datos d
  ) {
    return dto(s.guardar(id, d));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void eliminar(@PathVariable Long id) {
    s.eliminar(id);
  }
}
