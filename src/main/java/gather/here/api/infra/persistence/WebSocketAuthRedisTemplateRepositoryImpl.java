package gather.here.api.infra.persistence;

import gather.here.api.domain.entities.WebSocketAuth;
import gather.here.api.domain.etc.RedisTransaction;
import gather.here.api.domain.repositories.WebSocketAuthRepository;
import gather.here.api.domain.service.LocationShareService;
import gather.here.api.global.exception.ResponseStatus;
import gather.here.api.global.exception.WebSocketAuthException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class WebSocketAuthRedisTemplateRepositoryImpl implements WebSocketAuthRepository {
    private final RedisOperations<String, Object> redisOperations;


    @Override
    public void save(Consumer<LocationShareService> func, LocationShareService locationShareService, WebSocketAuth webSocketAuth) {
        RedisTransaction.transaction(redisOperations,operations -> {
            System.out.println("RedisTransaction.transaction");
            if(func != null) {
                func.accept(locationShareService);
            }
            save(webSocketAuth, operations);
        });
    }

    private static void save(WebSocketAuth webSocketAuth, RedisOperations<String, Object> operations) {
        String key = "webSocketAuth:" + webSocketAuth.getMemberSeq();
        Map<String, Object> data = new HashMap<>();
        data.put("sessionId", webSocketAuth.getSessionId());
        data.put("memberSeq", webSocketAuth.getMemberSeq());
        operations.opsForHash().putAll(key, data);
        operations.opsForHash().put("sessionIdIndex", webSocketAuth.getSessionId(), key);
        System.out.println("redis save");
    }

    public Optional<WebSocketAuth> findMemberSeq(Long memberSeq) {
        String key = "webSocketAuth:" + memberSeq;

        Map<Object, Object> data = redisOperations.opsForHash().entries(key);

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
        String key = (String) redisOperations.opsForHash().get("sessionIdIndex", sessionId);
        if(StringUtils.isEmpty(key)){
            throw new WebSocketAuthException(ResponseStatus.NOT_FOUND_SESSION_ID, HttpStatus.FORBIDDEN);
        }
        Map<Object, Object> data = redisOperations.opsForHash().entries(key);
        return WebSocketAuth.create(data.get("memberSeq"),data.get("sessionId"));
    }

    @Override
    public void deleteByMemberSeq(WebSocketAuth webSocketAuth) {
        String key = "webSocketAuth:" + webSocketAuth.getMemberSeq();
        if (redisOperations.hasKey(key)) {
            redisOperations.delete(key);
            redisOperations.opsForHash().delete("sessionIdIndex", webSocketAuth.getSessionId());
        } else {
            throw new WebSocketAuthException(ResponseStatus.NOT_FOUND_MEMBER, HttpStatus.FORBIDDEN);
        }
    }
}
