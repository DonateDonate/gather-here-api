package gather.here.api.domain.repositories;

import gather.here.api.Utils.Utils;
import gather.here.api.domain.entities.Member;
import gather.here.api.domain.entities.Room;
import gather.here.api.infra.persistence.jpa.MemberJpaRepository;
import gather.here.api.infra.persistence.jpa.RoomJpaRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@WebAppConfiguration
@SpringBootTest
@ActiveProfiles("test")
class RoomRepositoryTest {


    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private RoomJpaRepository roomRepository;

    @Autowired
    private EntityManager em;


    @PostConstruct
    public void init(){
        dataSetting(4,5);
    }

    @Test
    @Transactional
    public void jpaNPlusOneTest(){
        //arrange
        //act
        System.out.println("room findAll");
        //List<Room> all = roomRepository.findAll();

        roomRepository.findByStatus(1);
        System.out.println("===================================================");
    }

    @Transactional
    public void dataSetting(int roomCnt, int memberCnt){
        String password = "1234";
        for(int i = 0; i<roomCnt; i++){
            Room room = Room.create(1D,2D,"destination_"+i, "2024-10-14 19:22");
            roomRepository.save(room);
            for(int j=0; j<memberCnt; j++){
                Member member = Utils.createRandomMember(password);
                member.joinRoom(room);
                memberRepository.save(member);
            }
        }
    }
}