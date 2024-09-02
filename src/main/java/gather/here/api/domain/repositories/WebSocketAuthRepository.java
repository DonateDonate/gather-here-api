package gather.here.api.domain.repositories;

import gather.here.api.domain.entities.WebSocketAuth;

import java.util.List;

public interface WebSocketAuthRepository{
    void save(WebSocketAuth webSocketAuth);
    WebSocketAuth getByMemberSeq(Long memberSeq);
    WebSocketAuth getBySessionId(String sessionId);
    List<WebSocketAuth> findAll();
    void deleteByMemberSeq(Long memberSeq);
}
