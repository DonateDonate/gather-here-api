package gather.here.api.domain.repositories;

import gather.here.api.domain.entities.LocationShareEvent;
import gather.here.api.domain.entities.Room;

import java.util.Optional;

public interface RoomRepository {
    void save(Room room);
    Optional<Room> findByShareCode(String shareCode);
    void createLocationShareEvent(LocationShareEvent locationShareEvent);
    LocationShareEvent findByRoomSeq(Long roomSeq);

}
