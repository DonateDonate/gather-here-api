package gather.here.api.infra.persistence;

import gather.here.api.domain.entities.WebSocketAuth;
import gather.here.api.domain.repositories.WebSocketAuthRepository;
import gather.here.api.infra.persistence.redis.WebSocketAuthRedisRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class WebSocketAuthRepositoryImpl implements WebSocketAuthRepository {
    private final WebSocketAuthRedisRepository webSocketAuthRedisRepository;
    @Override
    public void save(WebSocketAuth webSocketAuth) {
        webSocketAuthRedisRepository.save(webSocketAuth);
    }

    @Override
    public Optional<WebSocketAuth> findByMemberSeq(Long memberSeq) {
        return webSocketAuthRedisRepository.findById(memberSeq);
    }

    @Override
    public List<WebSocketAuth> findAll() {
        return webSocketAuthRedisRepository.findAll();
    }

    @Override
    public WebSocketAuth findBySessionId(String sessionId) {
        return webSocketAuthRedisRepository.findBySessionId(sessionId);
    }

    @Override
    public void deleteByMemberSeq(Long memberSeq) {
        webSocketAuthRedisRepository.deleteById(memberSeq);
    }
}
