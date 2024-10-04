package gather.here.api.infra.persistence;

import gather.here.api.domain.entities.LocationShareEvent;
import gather.here.api.domain.repositories.LocationShareEventRepository;
import gather.here.api.global.exception.LocationShareException;
import gather.here.api.global.exception.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class LocationShareEventRedisTemplateRepositoryImpl implements LocationShareEventRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void save(LocationShareEvent locationShareEvent) {
        String key = "locationShareEvent:" + locationShareEvent.getRoomSeq();
        redisTemplate.opsForHash().putAll(key, convertToMap(locationShareEvent));
    }

    @Override
    public Optional<LocationShareEvent> findByRoomSeq(Long roomSeq) {
        String key = "locationShareEvent:" + roomSeq;
        Map<Object, Object> data = redisTemplate.opsForHash().entries(key);

        if (data.isEmpty()) {
            return Optional.empty();
        }
        try {
            Long retrievedRoomSeq = Long.valueOf(String.valueOf(data.get("roomSeq")));
            List<LocationShareEvent.MemberLocation> memberLocations = (List<LocationShareEvent.MemberLocation>) data.get("memberLocations");
            return Optional.of(LocationShareEvent.create(retrievedRoomSeq, memberLocations));
        } catch (ClassCastException e) {
            return Optional.empty();
        }
    }

    @Override
    public LocationShareEvent getByRoomSeq(Long roomSeq) {
        String key = "locationShareEvent:" + roomSeq;
        Map<Object, Object> data = redisTemplate.opsForHash().entries(key);

        if (data.isEmpty()) {
            throw new LocationShareException(ResponseStatus.NOT_FOUND_ROOM_SEQ,HttpStatus.FORBIDDEN);
        }
        try {
            Long retrievedRoomSeq = Long.valueOf(String.valueOf(data.get("roomSeq")));
            List<LocationShareEvent.MemberLocation> memberLocations = (List<LocationShareEvent.MemberLocation>) data.get("memberLocations");
            return LocationShareEvent.create(retrievedRoomSeq, memberLocations);
        } catch (ClassCastException e) {
            e.printStackTrace();
            throw new LocationShareException(ResponseStatus.INTERNAL_SERVER_ERROR, HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public void update(LocationShareEvent locationShareEvent) {
        delete(locationShareEvent);
        save(locationShareEvent);
    }

    @Override
    public void delete(LocationShareEvent locationShareEvent) {
        String key = "locationShareEvent:" + locationShareEvent.getRoomSeq();
        Boolean wasDeleted = redisTemplate.delete(key);
        if (wasDeleted == null || !wasDeleted) {
            throw new LocationShareException(ResponseStatus.NOT_FOUND_ROOM_SEQ,HttpStatus.FORBIDDEN);
        }
    }

    private Map<String, Object> convertToMap(LocationShareEvent locationShareEvent) {
        Map<String, Object> data = new HashMap<>();
        data.put("roomSeq", locationShareEvent.getRoomSeq());
        data.put("memberLocations", locationShareEvent.getMemberLocations());
        return data;
    }
}
