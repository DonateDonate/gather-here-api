package gather.here.api.domain.entities;

import gather.here.api.global.exception.ResponseStatus;
import gather.here.api.global.exception.WebSocketAuthException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@Getter
@RedisHash(value = "webSocketAuth", timeToLive = 86400L)
public class WebSocketAuth {
    @Id
    private Long memberSeq;

    @Indexed
    private String sessionId;

    public static WebSocketAuth create(Long memberSeq,String sessionId) {
        if(memberSeq == null){
            throw new WebSocketAuthException(ResponseStatus.NOT_FOUND_MEMBER, HttpStatus.FORBIDDEN);
        }
        if(StringUtils.isEmpty(sessionId)){
            throw new WebSocketAuthException(ResponseStatus.INVALID_REQUEST,HttpStatus.FORBIDDEN);
        }

        return new WebSocketAuth(memberSeq,sessionId);
    }

    public static WebSocketAuth create(Object memberSeq, Object sessionId) {
        if (memberSeq == null) {
            throw new WebSocketAuthException(ResponseStatus.NOT_FOUND_MEMBER, HttpStatus.FORBIDDEN);
        }
        Long memberSeqParsing;

        try {
            memberSeqParsing = Long.valueOf(String.valueOf(memberSeq));
        } catch (NumberFormatException e) {
            throw new WebSocketAuthException(ResponseStatus.INVALID_REQUEST, HttpStatus.FORBIDDEN);
        }

        String sessionIdParsing = String.valueOf(sessionId);
        if (StringUtils.isEmpty(sessionIdParsing)) {
            throw new WebSocketAuthException(ResponseStatus.INVALID_REQUEST, HttpStatus.FORBIDDEN);
        }

        return new WebSocketAuth(memberSeqParsing, sessionIdParsing);
    }

    private WebSocketAuth(Long memberSeq, String sessionId) {
        this.memberSeq = memberSeq;
        this.sessionId = sessionId;
    }
}
