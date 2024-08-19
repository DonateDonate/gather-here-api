package gather.here.api.domain.entities;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "webSocketAuth", timeToLive = 30)
public class WebSocketAuth {
    @Id
    private Long memberSeq;
    private String sessionId;

    public static WebSocketAuth create(Long memberSeq,String sessionId){
        return new WebSocketAuth(memberSeq,sessionId);
    }
    private WebSocketAuth(Long memberSeq, String sessionId) {
        this.memberSeq = memberSeq;
        this.sessionId = sessionId;
    }
}
