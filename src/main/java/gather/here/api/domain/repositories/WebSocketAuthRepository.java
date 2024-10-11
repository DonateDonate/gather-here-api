package gather.here.api.domain.repositories;

import gather.here.api.domain.entities.WebSocketAuth;
import gather.here.api.domain.service.LocationShareService;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public interface WebSocketAuthRepository{
    void save(Consumer<LocationShareService> func,LocationShareService locationShareService, WebSocketAuth webSocketAuth);
    Optional<WebSocketAuth> findMemberSeq(Long memberSeq);
    WebSocketAuth getBySessionId(String sessionId);
    List<WebSocketAuth> findAll();
    void deleteByMemberSeq(WebSocketAuth webSocketAuth);
}
