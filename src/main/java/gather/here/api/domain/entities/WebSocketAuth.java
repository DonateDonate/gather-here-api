package gather.here.api.domain.entities;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "webSocketAuth", timeToLive = 86400L)
public class WebSocketAuth {
    @Id
    private Long memberSeq;

    @Indexed
    private String sessionId;

    public static WebSocketAuth create(Long memberSeq,String sessionId){
        return new WebSocketAuth(memberSeq,sessionId);
    }
    private WebSocketAuth(Long memberSeq, String sessionId) {
        this.memberSeq = memberSeq;
        this.sessionId = sessionId;
    }
}
