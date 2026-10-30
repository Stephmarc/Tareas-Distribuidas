package pe.edu.upeu.orden.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.orden.entity.DetalleOrden;

public interface DetalleOrdenRepository
  extends JpaRepository<DetalleOrden, Long> {}
