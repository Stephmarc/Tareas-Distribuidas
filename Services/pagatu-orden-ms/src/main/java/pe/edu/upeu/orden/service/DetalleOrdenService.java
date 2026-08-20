package pe.edu.upeu.orden.service;

import pe.edu.upeu.orden.dto.DetalleOrdenRequest;
import pe.edu.upeu.orden.dto.DetalleOrdenResponse;
import pe.edu.upeu.orden.entity.DetalleOrden;
import pe.edu.upeu.orden.entity.Orden;
import pe.edu.upeu.orden.exception.ResourceNotFoundException;
import pe.edu.upeu.orden.mapper.DetalleOrdenMapper;
import pe.edu.upeu.orden.repository.DetalleOrdenRepository;
import pe.edu.upeu.orden.repository.OrdenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DetalleOrdenService {

    private final DetalleOrdenRepository detalleOrdenRepository;
    private final DetalleOrdenMapper detalleOrdenMapper;
    private final OrdenRepository ordenRepository;

    public List<DetalleOrdenResponse> listar() {
        return detalleOrdenRepository.findAll().stream()
                .map(detalleOrdenMapper::toResponse)
                .toList();
    }

    public DetalleOrdenResponse obtener(Long id) {
        return detalleOrdenMapper.toResponse(buscarOFallar(id));
    }

    public DetalleOrdenResponse crear(DetalleOrdenRequest request) {
        DetalleOrden detalle = detalleOrdenMapper.toEntity(request);
        detalle.setOrden(buscarOrdenOFallar(request.getOrdenId()));

        return detalleOrdenMapper.toResponse(
                detalleOrdenRepository.save(detalle)
        );
    }

    public DetalleOrdenResponse actualizar(
            Long id,
            DetalleOrdenRequest request) {

        DetalleOrden detalle = buscarOFallar(id);

        detalle.setProductoId(request.getProductoId());
        detalle.setCantidad(request.getCantidad());
        detalle.setPrecioUnitario(request.getPrecioUnitario());
        detalle.setOrden(buscarOrdenOFallar(request.getOrdenId()));

        return detalleOrdenMapper.toResponse(
                detalleOrdenRepository.save(detalle)
        );
    }

    public void eliminar(Long id) {
        detalleOrdenRepository.delete(buscarOFallar(id));
    }

    private DetalleOrden buscarOFallar(Long id) {
        return detalleOrdenRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Detalle de orden no encontrado: " + id));
    }

    private Orden buscarOrdenOFallar(Long ordenId) {
        return ordenRepository.findById(ordenId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Orden no encontrada: " + ordenId));
    }
}