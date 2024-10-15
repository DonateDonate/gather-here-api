package gather.here.api.domain.repositories;

import gather.here.api.Utils.Utils;
import gather.here.api.domain.entities.Member;
import gather.here.api.domain.entities.Room;
import gather.here.api.global.util.DateUtil;
import gather.here.api.infra.persistence.jpa.MemberJpaRepository;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@WebAppConfiguration
@SpringBootTest
@ActiveProfiles("test")
class RoomRepositoryTest {


    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private RoomRepository roomRepository;

    @PostConstruct
    public void init(){
        dataSetting(4,5);
    }

    @Test
    @Transactional
    public void jpaNPlusOneTest(){
        roomRepository.findAllByStatus(1);
    }

    @Transactional
    public void dataSetting(int roomCnt, int memberCnt){
        String password = "1234";
        for(int i = 0; i<roomCnt; i++){
            Room room = Room.create(1D,2D,"destination_"+i, DateUtil.convertLocalDateTimeToString(LocalDateTime.now().plusHours(12)));
            roomRepository.save(room);
            for(int j=0; j<memberCnt; j++){
                Member member = Utils.createRandomMember(password);
                member.joinRoom(room);
                memberRepository.save(member);
            }
        }
    }
}