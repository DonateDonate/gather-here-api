package gather.here.api.infra.persistence;

import gather.here.api.domain.entities.Room;
import gather.here.api.domain.repositories.RoomRepository;
import gather.here.api.global.exception.LocationShareException;
import gather.here.api.global.exception.ResponseStatus;
import gather.here.api.infra.persistence.jpa.RoomJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@RequiredArgsConstructor
public class RoomRepositoryImpl implements RoomRepository {
    private final RoomJpaRepository roomJpaRepository;

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
    public List<Room> findByStatus(int status) {
        return roomJpaRepository.findByStatus(status);
    }

}
