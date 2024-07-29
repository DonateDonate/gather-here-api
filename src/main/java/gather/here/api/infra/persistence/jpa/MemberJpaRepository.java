package gather.here.api.infra.persistence.jpa;

import gather.here.api.domain.entities.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<Member,Long> {
    Member findByIdentity(String identity);
}
