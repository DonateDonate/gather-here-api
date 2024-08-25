package gather.here.api.domain.repositories;

import gather.here.api.domain.entities.LocationShareEvent;
import gather.here.api.domain.entities.Room;

import java.util.Optional;

public interface RoomRepository {
    void save(Room room);
    Optional<Room> findByShareCode(String shareCode);
    void saveLocationShareEvent(LocationShareEvent locationShareEvent);
    Optional<LocationShareEvent> findLocationShareEventByRoomSeq(Long roomSeq);
    void updateLocationShareEvent(LocationShareEvent locationShareEvent);
    Iterable<LocationShareEvent> findAllLocationEvents();
    void removeLocationShareEventMember(LocationShareEvent locationShareEvent,Long memberSeq);
}
