package gather.here.api.application.service;

import gather.here.api.domain.entities.WebSocketAuth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class RoomServiceTest {
    @Autowired
    private RoomService roomService;

    @Test
    public void webSocketSaveTest(){
        WebSocketAuth webSocketAuth = WebSocketAuth.create(213L,"123123123123");

    }


}