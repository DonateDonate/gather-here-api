package gather.here.api.infra.persistence;

import gather.here.api.domain.entities.Room;
import gather.here.api.domain.repositories.RoomRepository;
import gather.here.api.infra.persistence.jpa.RoomJpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class RoomRepositoryImpl implements RoomRepository {
    private final RoomJpaRepository roomJpaRepository;

    @Override
    public void save(Room room) {
        roomJpaRepository.save(room);
    }

    @Override
    public Optional<Room> findByShareCode(String shareCode){
        return Optional.ofNullable(roomJpaRepository.findByShareCode(shareCode));
    }
}
