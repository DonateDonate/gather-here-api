package gather.here.api.application.service;

import gather.here.api.domain.entities.WebSocketAuth;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
class RoomServiceTest {
    @Autowired
    private RoomService roomService;

//    @Test
//    public void webSocketSaveTest(){
//        List<WebSocketAuth> byAllWebSocketAuth = roomService.findByAllWebSocketAuth();
//        Assertions.assertThat(byAllWebSocketAuth).isNotNull();
//
//    }


}