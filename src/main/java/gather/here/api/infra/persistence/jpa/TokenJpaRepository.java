package gather.here.api.infra.persistence.jpa;

import gather.here.api.domain.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenJpaRepository extends JpaRepository<Token,Long> {
}
