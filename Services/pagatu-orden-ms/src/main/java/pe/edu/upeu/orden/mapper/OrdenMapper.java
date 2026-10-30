package pe.edu.upeu.orden.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.edu.upeu.orden.dto.OrdenRequest;
import pe.edu.upeu.orden.dto.OrdenResponse;
import pe.edu.upeu.orden.entity.Orden;

@Mapper(componentModel = "spring", uses = DetalleOrdenMapper.class)
public interface OrdenMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "detalles", ignore = true)
  Orden toEntity(OrdenRequest request);

  OrdenResponse toResponse(Orden orden);
}
