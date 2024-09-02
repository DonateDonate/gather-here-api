package gather.here.api.domain.entities;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WebSocketAuthTest {

    @DisplayName("sut는 성공적으로 webSocketAuth 인스턴스를 생성한다")
    @Test
    public void successCreateWebSocketAuthTest(){
        //arrange
        WebSocketAuth sut = new WebSocketAuth();
        Long memberSeq = 1L;
        String sessionId = String.valueOf(UUID.randomUUID());

        //act
        WebSocketAuth webSocketAuth = sut.create(memberSeq, sessionId);

        //assert
        Assertions.assertThat(webSocketAuth.getMemberSeq()).isEqualTo(memberSeq);
        Assertions.assertThat(webSocketAuth.getSessionId()).isEqualTo(sessionId);

    }


}