package gather.here.api.domain.repositories;

import gather.here.api.domain.entities.LocationShareEvent;
import gather.here.api.domain.entities.Room;

import java.util.List;
import java.util.Optional;

public interface RoomRepository {
    void save(Room room);
    Room getByShareCode(String shareCode);
    void saveLocationShareEvent(LocationShareEvent locationShareEvent);
    Optional<LocationShareEvent> findLocationShareEventByRoomSeq(Long roomSeq);
    LocationShareEvent getLocationShareEventByRoomSeq(Long roomSeq);
    void updateLocationShareEvent(LocationShareEvent locationShareEvent);
    Iterable<LocationShareEvent> findAllLocationEvents();
    void deleteLocationShareEvent(LocationShareEvent locationShareEvent);
    List<Room> findByStatus(int status);
}
