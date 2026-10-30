package pe.edu.upeu.orden.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.edu.upeu.orden.dto.DetalleOrdenRequest;
import pe.edu.upeu.orden.dto.DetalleOrdenResponse;
import pe.edu.upeu.orden.entity.DetalleOrden;

@Mapper(componentModel = "spring")
public interface DetalleOrdenMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "orden", ignore = true)
  @Mapping(target = "nombreProducto", ignore = true)
  DetalleOrden toEntity(DetalleOrdenRequest request);

  @Mapping(target = "ordenId", source = "orden.id")
  DetalleOrdenResponse toResponse(DetalleOrden detalleOrden);
}
