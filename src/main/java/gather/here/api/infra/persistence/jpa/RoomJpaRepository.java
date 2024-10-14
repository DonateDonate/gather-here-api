package gather.here.api.infra.persistence.jpa;

import gather.here.api.domain.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomJpaRepository extends JpaRepository<Room,Long> {
    Room findByShareCode(String shareCode);
    List<Room>findByStatus(int status);
}
