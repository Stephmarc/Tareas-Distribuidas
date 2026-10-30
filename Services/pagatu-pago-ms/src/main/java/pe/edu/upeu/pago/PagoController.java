package pe.edu.upeu.pago;

import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/pagos")
public class PagoController {

  private final EntityManager em;

  public PagoController(EntityManager em) {
    this.em = em;
  }

  @GetMapping("/orden/{id}")
  public Object obtener(
    @PathVariable Long id,
    @AuthenticationPrincipal Jwt jwt
  ) {
    List<?> filas = em
      .createNativeQuery(
        "SELECT orden_id,total,estado FROM pagos WHERE orden_id=:id AND cliente_id=:cliente"
      )
      .setParameter("id", id)
      .setParameter("cliente", jwt.getSubject())
      .getResultList();
    if (filas.isEmpty()) throw new ResponseStatusException(
      HttpStatus.NOT_FOUND
    );
    Object[] f = (Object[]) filas.getFirst();
    return java.util.Map.of("ordenId", f[0], "total", f[1], "estado", f[2]);
  }
}
