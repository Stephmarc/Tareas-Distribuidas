package pe.edu.upeu.orden.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.orden.dto.OrdenRequest;
import pe.edu.upeu.orden.dto.OrdenResponse;
import pe.edu.upeu.orden.entity.Orden;
import pe.edu.upeu.orden.exception.ResourceNotFoundException;
import pe.edu.upeu.orden.mapper.OrdenMapper;
import pe.edu.upeu.orden.repository.OrdenRepository;

@Service
@RequiredArgsConstructor
public class OrdenService {

  private final OrdenRepository ordenRepository;
  private final OrdenMapper ordenMapper;

  public List<OrdenResponse> listar() {
    return ordenRepository
      .findAllConDetalles()
      .stream()
      .map(ordenMapper::toResponse)
      .toList();
  }

  public OrdenResponse obtener(Long id) {
    return ordenMapper.toResponse(buscarOFallar(id));
  }

  public OrdenResponse crear(OrdenRequest request) {
    Orden orden = ordenMapper.toEntity(request);
    return ordenMapper.toResponse(ordenRepository.save(orden));
  }

  public OrdenResponse actualizar(Long id, OrdenRequest request) {
    Orden orden = buscarOFallar(id);

    orden.setClienteId(request.getClienteId());
    orden.setEstado(request.getEstado());
    orden.setTotal(request.getTotal());

    return ordenMapper.toResponse(ordenRepository.save(orden));
  }

  public void eliminar(Long id) {
    ordenRepository.delete(buscarOFallar(id));
  }

  private Orden buscarOFallar(Long id) {
    return ordenRepository
      .findById(id)
      .orElseThrow(() ->
        new ResourceNotFoundException("Orden no encontrada: " + id)
      );
  }
}
