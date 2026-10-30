package pe.edu.upeu.catalogo;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.catalogo.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {}
