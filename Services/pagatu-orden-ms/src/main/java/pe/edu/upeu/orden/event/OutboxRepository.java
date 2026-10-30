package pe.edu.upeu.orden.event;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxRepository extends JpaRepository<Outbox, Long> {
  List<Outbox> findTop20ByEnviadoFalseOrderByIdAsc();
}
