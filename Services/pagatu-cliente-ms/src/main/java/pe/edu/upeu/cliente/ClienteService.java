package pe.edu.upeu.cliente;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ClienteService {

  private final ClienteRepository r;

  public ClienteService(ClienteRepository r) {
    this.r = r;
  }

  public List<Cliente> listar() {
    return r.findAll();
  }

  public Cliente obtener(Long id) {
    return r
      .findById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  public Cliente guardar(Long id, ClienteController.Datos d) {
    Cliente c = id == null ? new Cliente() : obtener(id);
    c.nombre = d.nombre();
    c.documento = d.documento();
    return r.save(c);
  }

  public void eliminar(Long id) {
    r.delete(obtener(id));
  }
}
