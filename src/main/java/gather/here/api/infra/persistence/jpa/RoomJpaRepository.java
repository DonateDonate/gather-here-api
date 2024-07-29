package gather.here.api.infra.persistence.jpa;

import gather.here.api.domain.entities.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomJpaRepository extends JpaRepository<Member,Long> {
}
