package gather.here.api.domain.repositories;

import gather.here.api.domain.entities.WebSocketAuth;

import java.util.List;
import java.util.Optional;

public interface WebSocketAuthRepository{
    void save(WebSocketAuth webSocketAuth);
    Optional<WebSocketAuth> findMemberSeq(Long memberSeq);
    WebSocketAuth getBySessionId(String sessionId);
    List<WebSocketAuth> findAll();
    void deleteByMemberSeq(Long memberSeq);
}
