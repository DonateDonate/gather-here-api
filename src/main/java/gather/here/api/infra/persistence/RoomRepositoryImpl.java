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
    public void createLocationShareEvent(LocationShareEvent locationShareEvent) {
        locationShareEventRedisRepository.save(locationShareEvent);
    }

    @Override
    public LocationShareEvent findByRoomSeq(Long roomSeq) {
        return locationShareEventRedisRepository.findByRoomSeq(roomSeq);
    }
}
