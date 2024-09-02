package gather.here.api.infra.persistence;

import gather.here.api.domain.entities.WebSocketAuth;
import gather.here.api.domain.repositories.WebSocketAuthRepository;
import gather.here.api.global.exception.ResponseStatus;
import gather.here.api.global.exception.WebSocketAuthException;
import gather.here.api.infra.persistence.redis.WebSocketAuthRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@RequiredArgsConstructor
public class WebSocketAuthRepositoryImpl implements WebSocketAuthRepository {
    private final WebSocketAuthRedisRepository webSocketAuthRedisRepository;
    @Override
    public void save(WebSocketAuth webSocketAuth) {
        webSocketAuthRedisRepository.save(webSocketAuth);
    }

    @Override
    public WebSocketAuth getByMemberSeq(Long memberSeq) {
        return webSocketAuthRedisRepository.findById(memberSeq).orElseThrow(
                () -> new WebSocketAuthException(ResponseStatus.NOT_FOUND_ROOM_SEQ , HttpStatus.FORBIDDEN)
        );
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
    public void deleteByMemberSeq(Long memberSeq) {
        webSocketAuthRedisRepository.deleteById(memberSeq);
    }
}
