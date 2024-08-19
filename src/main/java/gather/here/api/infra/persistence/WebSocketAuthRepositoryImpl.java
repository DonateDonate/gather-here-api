package gather.here.api.infra.persistence;

import gather.here.api.domain.entities.WebSocketAuth;
import gather.here.api.domain.repositories.WebSocketAuthRepository;
import gather.here.api.infra.persistence.redis.WebSocketAuthRedisRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class WebSocketAuthRepositoryImpl implements WebSocketAuthRepository {
    private final WebSocketAuthRedisRepository webSocketAuthRedisRepository;
    @Override
    public void save(WebSocketAuth webSocketAuth) {
        webSocketAuthRedisRepository.save(webSocketAuth);
    }

    @Override
    public WebSocketAuth findByMemberSeq(Long memberSeq) {
        return webSocketAuthRedisRepository.findByMemberSeq(memberSeq);
    }

    @Override
    public List<WebSocketAuth> findAll() {
        return webSocketAuthRedisRepository.findAll();
    }
}
