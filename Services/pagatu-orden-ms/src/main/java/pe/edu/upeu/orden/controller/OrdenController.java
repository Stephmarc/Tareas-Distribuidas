package pe.edu.upeu.orden.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upeu.orden.dto.*;
import pe.edu.upeu.orden.service.*;

@RestController
@RequestMapping("/api/v1/ordenes")
@RequiredArgsConstructor
public class OrdenController {

  private final OrdenService service;
  private final RegistroOrdenService registro;

  private boolean admin(Jwt jwt) {
    var a = jwt.getClaimAsMap("realm_access");
    return (
      a != null &&
      ((List<?>) a.getOrDefault("roles", List.of())).contains("ADMIN")
    );
  }

  @GetMapping
  public List<OrdenResponse> listar(@AuthenticationPrincipal Jwt jwt) {
    return service
      .listar()
      .stream()
      .filter(o -> admin(jwt) || jwt.getSubject().equals(o.getClienteId()))
      .toList();
  }

  @GetMapping("/{id}")
  public OrdenResponse obtener(
    @PathVariable Long id,
    @AuthenticationPrincipal Jwt jwt
  ) {
    var o = service.obtener(id);
    if (
      !admin(jwt) && !jwt.getSubject().equals(o.getClienteId())
    ) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    return o;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public OrdenResponse crear(
    @Valid @RequestBody CrearOrdenRequest request,
    @AuthenticationPrincipal Jwt jwt
  ) {
    return registro.crear(request, jwt.getSubject());
  }
}
