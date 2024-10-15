package gather.here.api.infra.persistence;

import com.querydsl.jpa.impl.JPAQueryFactory;
import gather.here.api.domain.entities.Room;
import gather.here.api.infra.persistence.jpa.RoomCustomRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class RoomCustomRepositoryImpl implements RoomCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Room> findAllByStatus(int status) {
//                jpaQueryFactory
//                .selectFrom()
//                .where(Room.room.status.eq(status))
//                .fetch();

        return List.of();
    }
}
