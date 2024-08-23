package gather.here.api.infra.persistence;

import gather.here.api.domain.entities.LocationShareEvent;
import gather.here.api.domain.entities.Room;
import gather.here.api.domain.repositories.RoomRepository;
import gather.here.api.infra.persistence.jpa.RoomJpaRepository;
import gather.here.api.infra.persistence.redis.LocationShareEventRedisRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class RoomRepositoryImpl implements RoomRepository {
    private final RoomJpaRepository roomJpaRepository;
    private final LocationShareEventRedisRepository locationShareEventRedisRepository;


    @Override
    public void save(Room room) {
        roomJpaRepository.save(room);
    }

    @Override
    public Optional<Room> findByShareCode(String shareCode){
        return Optional.ofNullable(roomJpaRepository.findByShareCode(shareCode));
    }

    @Override
    public void saveLocationShareEvent(LocationShareEvent locationShareEvent) {
        locationShareEventRedisRepository.save(locationShareEvent);
    }

    @Override
    public Optional<LocationShareEvent> findLocationShareEventByRoomSeq(Long roomSeq) {
        return locationShareEventRedisRepository.findById(roomSeq);
    }

    @Override
    public void updateLocationShareEvent(LocationShareEvent locationShareEvent){
        locationShareEventRedisRepository.delete(locationShareEvent);
        locationShareEventRedisRepository.save(locationShareEvent);
    }

    @Override
    public Iterable<LocationShareEvent> findAllLocationEvents() {
        return locationShareEventRedisRepository.findAll();
    }
}
