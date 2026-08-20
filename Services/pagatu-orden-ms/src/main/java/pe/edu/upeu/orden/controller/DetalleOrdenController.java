package pe.edu.upeu.orden.controller;

import pe.edu.upeu.orden.dto.DetalleOrdenRequest;
import pe.edu.upeu.orden.dto.DetalleOrdenResponse;
import pe.edu.upeu.orden.service.DetalleOrdenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/detalle-ordenes")
@RequiredArgsConstructor
public class DetalleOrdenController {

    private final DetalleOrdenService detalleOrdenService;

    @GetMapping
    public List<DetalleOrdenResponse> listar() {
        return detalleOrdenService.listar();
    }

    @GetMapping("/{id}")
    public DetalleOrdenResponse obtener(@PathVariable Long id) {
        return detalleOrdenService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DetalleOrdenResponse crear(
            @Valid @RequestBody DetalleOrdenRequest request) {
        return detalleOrdenService.crear(request);
    }

    @PutMapping("/{id}")
    public DetalleOrdenResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody DetalleOrdenRequest request) {
        return detalleOrdenService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        detalleOrdenService.eliminar(id);
    }
}