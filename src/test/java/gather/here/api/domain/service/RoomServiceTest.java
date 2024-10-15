package gather.here.api.domain.service;

import gather.here.api.domain.repositories.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringBootTest
@ActiveProfiles("test")
@WebAppConfiguration
class RoomServiceTest {

    @Autowired
    private RoomService roomService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("sut는 존재하는 room이 있으면 예외가 발생한다")
    public void createRoomAlreadyExistTest(){
        //arrange



        //act

        //assert
    }


}