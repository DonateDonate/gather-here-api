package gather.here.api.domain.repositories;

import gather.here.api.domain.entities.Room;

import java.util.List;

public interface RoomRepository {
    void save(Room room);

    Room getByShareCode(String shareCode);

    List<Room> findByStatus(int status);
}
