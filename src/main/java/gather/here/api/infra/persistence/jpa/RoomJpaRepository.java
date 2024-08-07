package gather.here.api.infra.persistence.jpa;

import gather.here.api.domain.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomJpaRepository extends JpaRepository<Room,Long> {
}
