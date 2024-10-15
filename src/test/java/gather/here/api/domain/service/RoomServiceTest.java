package gather.here.api.domain.service;

import gather.here.api.Utils.Utils;
import gather.here.api.domain.entities.Member;
import gather.here.api.domain.entities.Room;
import gather.here.api.domain.repositories.MemberRepository;
import gather.here.api.domain.repositories.RoomRepository;
import gather.here.api.domain.service.dto.request.RoomCreateRequestDto;
import gather.here.api.global.exception.RoomException;
import gather.here.api.global.util.DateUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@WebAppConfiguration
class RoomServiceTest {

    @Autowired
    private RoomService roomService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Test
    @DisplayName("sut는 존재하는 room이 있으면 예외가 발생한다")
    @Transactional
    public void createRoomAlreadyExistTest(){
        //arrange
        String password = "1234";
        Member member = Utils.createRandomMember(password);
        memberRepository.save(member);
        Room room = Utils.createRandomRoom();
        roomRepository.save(room);
        member.joinRoom(room);
        RoomService sut = roomService;
        RoomException actual = null;
        RoomCreateRequestDto request = new RoomCreateRequestDto(
                30D,30D,"목적지",Utils.localDatePlus12Hours()
        );
        //act
        try {
            sut.createRoom(request, member.getSeq());
        }catch (RoomException e){
            actual = e;
        }

        //assert
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual).isInstanceOf(RoomException.class);
    }

    @Test
    @DisplayName("sut는 성공적으로 createRoom 객체를 생성한다")
    @Transactional
    public void successCreateRoomTest(){
        //arrange
        String password = "1234";
        Member member = Utils.createRandomMember(password);
        memberRepository.save(member);
        RoomService sut = roomService;
        RoomCreateRequestDto request = new RoomCreateRequestDto(
                30D,30D,"목적지",Utils.localDatePlus12Hours()
        );
        Room actual = null;
        //act
        sut.createRoom(request,member.getSeq());
        actual = memberRepository.getBySeq(member.getSeq()).getRoom();

        //assert
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getEncounterDate()).isEqualTo(DateUtil.convertToLocalDateTime(request.getEncounterDate()));
        Assertions.assertThat(actual.getDestinationLat()).isEqualTo(request.getDestinationLat());
        Assertions.assertThat(actual.getDestinationName()).isEqualTo(request.getDestinationName());
        Assertions.assertThat(actual.getDestinationLng()).isEqualTo(request.getDestinationLng());
    }
    




}