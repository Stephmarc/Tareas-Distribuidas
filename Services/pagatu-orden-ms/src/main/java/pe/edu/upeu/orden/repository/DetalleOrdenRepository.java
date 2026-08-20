package pe.edu.upeu.orden.repository;

import pe.edu.upeu.orden.entity.DetalleOrden;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Long> {
}