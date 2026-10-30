package pe.edu.upeu.orden.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.orden.dto.DetalleOrdenResponse;
import pe.edu.upeu.orden.service.DetalleOrdenService;

@RestController
@RequestMapping("/api/v1/detalles-orden")
@RequiredArgsConstructor
public class DetalleOrdenController {

  private final DetalleOrdenService service;

  @GetMapping
  public List<DetalleOrdenResponse> listar() {
    return service.listar();
  }

  @GetMapping("/{id}")
  public DetalleOrdenResponse obtener(@PathVariable Long id) {
    return service.obtener(id);
  }
}
