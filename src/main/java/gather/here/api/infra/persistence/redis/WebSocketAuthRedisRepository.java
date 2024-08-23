package gather.here.api.infra.persistence.redis;

import gather.here.api.domain.entities.WebSocketAuth;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WebSocketAuthRedisRepository extends CrudRepository<WebSocketAuth,Long> {
    List<WebSocketAuth> findAll();
    WebSocketAuth findBySessionId(String sessionId);
    void deleteByMemberSeq(Long memberSeq);
}
