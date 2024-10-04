package gather.here.api.infra.persistence;

import gather.here.api.domain.entities.WebSocketAuth;
import gather.here.api.domain.repositories.WebSocketAuthRepository;
import gather.here.api.global.exception.ResponseStatus;
import gather.here.api.global.exception.WebSocketAuthException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class WebSocketAuthRedisTemplateRepositoryImpl implements WebSocketAuthRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void save(WebSocketAuth webSocketAuth) {
        String key = "webSocketAuth:" + webSocketAuth.getMemberSeq();
        Map<String, Object> data = new HashMap<>();
        data.put("sessionId", webSocketAuth.getSessionId());
        data.put("memberSeq", webSocketAuth.getMemberSeq());
        redisTemplate.opsForHash().putAll(key, data);
        redisTemplate.opsForHash().put("sessionIdIndex", webSocketAuth.getSessionId(), key);
    }

    public Optional<WebSocketAuth> findMemberSeq(Long memberSeq) {
        String key = "webSocketAuth:" + memberSeq;

        Map<Object, Object> data = redisTemplate.opsForHash().entries(key);

        if (data.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(WebSocketAuth.create(
                data.get("memberSeq"),
                data.get("sessionId")
        ));
    }

    @Override
    public List<WebSocketAuth> findAll() {
        return null;
    }

    @Override
    public WebSocketAuth getBySessionId(String sessionId) {
        String key = (String) redisTemplate.opsForHash().get("sessionIdIndex", sessionId);
        if(StringUtils.isEmpty(key)){
            throw new WebSocketAuthException(ResponseStatus.NOT_FOUND_SESSION_ID, HttpStatus.FORBIDDEN);
        }
        Map<Object, Object> data = redisTemplate.opsForHash().entries(key);
        return WebSocketAuth.create(data.get("memberSeq"),data.get("sessionId"));
    }

    @Override
    public void deleteByMemberSeq(WebSocketAuth webSocketAuth) {
        String key = "webSocketAuth:" + webSocketAuth.getMemberSeq();
        if (redisTemplate.hasKey(key)) {
            redisTemplate.delete(key);
            redisTemplate.opsForHash().delete("sessionIdIndex", webSocketAuth.getSessionId());
        } else {
            throw new WebSocketAuthException(ResponseStatus.NOT_FOUND_MEMBER, HttpStatus.FORBIDDEN);
        }
    }
}
