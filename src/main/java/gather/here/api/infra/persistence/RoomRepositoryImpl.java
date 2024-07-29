package gather.here.api.infra.persistence;

import gather.here.api.domain.repositories.RoomRepository;
import gather.here.api.infra.persistence.jpa.RoomJpaRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RoomRepositoryImpl implements RoomRepository {
    private final RoomJpaRepository roomJpaRepository;
}
