package gather.here.api.domain.repositories;

import gather.here.api.domain.entities.LocationShareEvent;

import java.util.Optional;

public interface LocationShareEventRepository {
    void save(LocationShareEvent locationShareEvent);
    Optional<LocationShareEvent> findByRoomSeq(Long roomSeq);
    LocationShareEvent getByRoomSeq(Long roomSeq);
    void update(LocationShareEvent locationShareEvent);
    void delete(LocationShareEvent locationShareEvent);
}
