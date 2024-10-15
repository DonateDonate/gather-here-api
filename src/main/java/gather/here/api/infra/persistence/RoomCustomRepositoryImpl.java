package gather.here.api.infra.persistence;

import com.querydsl.jpa.impl.JPAQueryFactory;
import gather.here.api.domain.entities.QMember;
import gather.here.api.domain.entities.QRoom;
import gather.here.api.domain.entities.Room;
import gather.here.api.infra.persistence.jpa.RoomCustomRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class RoomCustomRepositoryImpl implements RoomCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Room> findAllByStatus(int status) {
        return jpaQueryFactory
                .selectFrom(QRoom.room)
                .leftJoin(QRoom.room.memberList, QMember.member).fetchJoin()
                .where(QRoom.room.status.eq(status))
                .stream().toList();
    }
}
