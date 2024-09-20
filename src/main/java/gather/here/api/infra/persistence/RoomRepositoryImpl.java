package gather.here.api.infra.persistence;

import gather.here.api.domain.entities.LocationShareEvent;
import gather.here.api.domain.entities.Room;
import gather.here.api.domain.repositories.RoomRepository;
import gather.here.api.global.exception.LocationShareException;
import gather.here.api.global.exception.ResponseStatus;
import gather.here.api.infra.persistence.jpa.RoomJpaRepository;
import gather.here.api.infra.persistence.redis.LocationShareEventRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;
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
    public Room getByShareCode(String shareCode) {
        Room room = roomJpaRepository.findByShareCode(shareCode);
        if(room == null){
            throw new LocationShareException(ResponseStatus.NOT_FOUND_SHARE_CODE, HttpStatus.FORBIDDEN);
        }
        return room;
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
    public LocationShareEvent getLocationShareEventByRoomSeq(Long roomSeq) {
        return locationShareEventRedisRepository.findById(roomSeq).orElseThrow(
                () -> new LocationShareException(ResponseStatus.NOT_FOUND_LOCATION_SHARE_EVENT,HttpStatus.FORBIDDEN)
        );
    }

    @Override
    public void updateLocationShareEvent(LocationShareEvent locationShareEvent){
        locationShareEventRedisRepository.deleteById(locationShareEvent.getRoomSeq());
        locationShareEventRedisRepository.save(locationShareEvent);
    }

    @Override
    public Iterable<LocationShareEvent> findAllLocationEvents() {
        return locationShareEventRedisRepository.findAll();
    }

    @Override
    public void deleteLocationShareEvent(LocationShareEvent locationShareEvent) {
        locationShareEventRedisRepository.deleteById(locationShareEvent.getRoomSeq());
    }

    @Override
    public List<Room> findByStatus(int status) {
        return roomJpaRepository.findByStatus(status);
    }
}
