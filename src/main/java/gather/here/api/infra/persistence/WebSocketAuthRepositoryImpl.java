package gather.here.api.infra.persistence;

import gather.here.api.domain.entities.WebSocketAuth;
import gather.here.api.domain.repositories.WebSocketAuthRepository;
import gather.here.api.domain.service.LocationShareService;
import gather.here.api.global.exception.ResponseStatus;
import gather.here.api.global.exception.WebSocketAuthException;
import gather.here.api.infra.persistence.redis.WebSocketAuthRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class WebSocketAuthRepositoryImpl implements WebSocketAuthRepository {
    private final WebSocketAuthRedisRepository webSocketAuthRedisRepository;

    @Override
    public void save(Consumer<LocationShareService> func, LocationShareService locationShareService, WebSocketAuth webSocketAuth) {
        webSocketAuthRedisRepository.save(webSocketAuth);
    }

    public Optional<WebSocketAuth> findMemberSeq(Long memberSeq) {
        return webSocketAuthRedisRepository.findById(memberSeq);
    }

    @Override
    public List<WebSocketAuth> findAll() {
        return webSocketAuthRedisRepository.findAll();
    }

    @Override
    public WebSocketAuth getBySessionId(String sessionId) {
        WebSocketAuth webSocketAuth = webSocketAuthRedisRepository.findBySessionId(sessionId);
        if(webSocketAuth == null){
            throw new WebSocketAuthException(ResponseStatus.NOT_FOUND_SESSION_ID, HttpStatus.FORBIDDEN);
        }
        return webSocketAuth;
    }

    @Override
    public void deleteByMemberSeq(WebSocketAuth webSocketAuth) {
        webSocketAuthRedisRepository.deleteById(webSocketAuth.getMemberSeq());
    }
}
