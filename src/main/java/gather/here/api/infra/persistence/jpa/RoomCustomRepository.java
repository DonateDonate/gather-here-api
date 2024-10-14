package gather.here.api.infra.persistence.jpa;

import gather.here.api.domain.entities.Room;

import java.util.List;

public interface RoomCustomRepository {
    List<Room> findAllByStatus(int status);
}
