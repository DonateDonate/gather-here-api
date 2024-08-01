package gather.here.api.infra.persistence.jpa;

import gather.here.api.domain.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken,Long> {
    List<RefreshToken> findByIdentity(String identity);
}
