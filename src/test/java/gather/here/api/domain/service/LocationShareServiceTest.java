package gather.here.api.domain.service;

import gather.here.api.Utils.Utils;
import gather.here.api.domain.dobules.FileFactoryStub;
import gather.here.api.domain.entities.LocationShareEvent;
import gather.here.api.domain.entities.Member;
import gather.here.api.domain.entities.Room;
import gather.here.api.domain.entities.WebSocketAuth;
import gather.here.api.domain.repositories.LocationShareEventRepository;
import gather.here.api.domain.repositories.MemberRepository;
import gather.here.api.domain.repositories.RoomRepository;
import gather.here.api.domain.repositories.WebSocketAuthRepository;
import gather.here.api.domain.service.dto.request.JoinRoomRequestDto;
import gather.here.api.domain.service.dto.request.LocationShareEventRequestDto;
import gather.here.api.domain.service.dto.request.MemberSignUpRequestDto;
import gather.here.api.domain.service.dto.request.RoomCreateRequestDto;
import gather.here.api.global.exception.ResponseStatus;
import gather.here.api.global.exception.RoomException;
import gather.here.api.global.exception.WebSocketAuthException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static gather.here.api.global.util.DateUtil.convertLocalDateTimeToString;

@WebAppConfiguration
@SpringBootTest
@ActiveProfiles("test")
class LocationShareServiceTest {

    @Autowired
    private WebSocketAuthRepository webSocketAuthRepository;

    @Autowired
    private RoomService roomService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LocationShareEventRepository locationShareEventRepository;


    @DisplayName("sut는 참가한 room이 없으면 예외가 발생한다")
    @Test
    public void duplicateSaveWebSocketAuthTest(){
        //arrange
        String sessionId = String.valueOf(UUID.randomUUID());
        Member member = createMember();
        LocationShareService sut = new LocationShareService(webSocketAuthRepository,memberRepository, new FileFactoryStub(),locationShareEventRepository);

        WebSocketAuth webSocketAuth = WebSocketAuth.create(member.getSeq(), sessionId);
        webSocketAuthRepository.save(webSocketAuth);
        RoomException actual = null;

        //act
        try {
            sut.saveWebSocketAuth(sessionId, member.getSeq());
        }catch (RoomException e){
            actual = e;
        }

        //assert
        Assertions.assertThat(actual.getResponseStatus().getCode()).isEqualTo(ResponseStatus.CLOSED_ROOM.getCode());
    }

    @DisplayName("sut는 성공적으로 webSocketAuth를 저장한다")
    @Test
    @Transactional
    public void successWebSocketAuth(){
        //arrange
        String sessionId = String.valueOf(UUID.randomUUID());
        Member member = createMember();

        Double destinationLat = 45.2;
        Double destinationLng = 77.7;
        String destinationName = "산하네집";
        Date date = new Date();
        LocalDateTime localDateTime = date.toInstant()
                .atZone(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime()
                .plusHours(1);

        String encounterDate = convertLocalDateTimeToString(localDateTime);
        Room room = Room.create(destinationLat,destinationLng,destinationName,encounterDate,member);
        roomRepository.save(room);
        member.setRoom(room);

 LocationShareService sut = new LocationShareService(webSocketAuthRepository,memberRepository, new FileFactoryStub(),locationShareEventRepository);
        WebSocketAuth actual = null;

        //act
        sut.saveWebSocketAuth(sessionId, member.getSeq());

        //assert
        actual = webSocketAuthRepository.getBySessionId(sessionId);
        Assertions.assertThat(actual.getMemberSeq()).isEqualTo(member.getSeq());
        Assertions.assertThat(actual.getSessionId()).isEqualTo(sessionId);
    }

//    @DisplayName("sut는 locationShareEvent에 중복된 roomSeq값이 있으면 예외가 발생한다")
//    @Test
//    @Transactional
//    public void duplicateCreateTypeHandleActionTest(){
//        //arrange
//        String identity =  Utils.randomMemberId();
//        String password = "1234";
//        String sessionId = String.valueOf(UUID.randomUUID());
//        Double destinationLat = 45.2;
//        Double destinationLng = 77.7;
//        String destinationName = "산하네집";
//        Date date = new Date();
//        LocalDateTime localDateTime = date.toInstant()
//                .atZone(ZoneId.of("Asia/Seoul"))
//                .toLocalDateTime()
//                .plusHours(1);
//
//        String encounterDate = convertLocalDateTimeToString(localDateTime);
//        LocationShareException actual = null;
//
//        //member 가입
//        MemberSignUpRequestDto memberSignUpRequestDto = new MemberSignUpRequestDto(identity, password);
//        memberService.save(memberSignUpRequestDto);
//
//        Member member = memberRepository.findByIdentityAndIsActiveTrue(identity).get();
//
//
//        //room create
//        RoomCreateRequestDto roomCreateRequestDto = new RoomCreateRequestDto(
//                destinationLat,
//                destinationLng,
//                destinationName,
//                encounterDate
//        );
//        roomService.createRoom(
//            roomCreateRequestDto,
//                member.getSeq()
//        );
//
//        WebSocketAuth webSocketAuth = WebSocketAuth.create(member.getSeq(),sessionId);
//        webSocketAuthRepository.save(webSocketAuth);
//
//        int type =0;
//        Double presentLat = 12.3;
//        Double presentLng = 46.2;
//        Double destinationDistance = 41.4;
//        LocationShareEventRequestDto request =
//                new LocationShareEventRequestDto(type,presentLat,presentLng,destinationDistance);
//
// LocationShareService sut = new LocationShareService(webSocketAuthRepository,memberRepository, new FileFactoryStub(),locationShareEventRepository);
//
//        LocationShareEvent locationShareEvent
//                = new LocationShareEvent().create(
//                        member.getRoom().getSeq(),
//                        member.getSeq(),
//                        sessionId,
//                        member.getNickname(),
//                        member.getImageKey(),
//                        presentLat,
//                        presentLng,
//                        destinationDistance,
//                        null
//        );
//        roomRepository.saveLocationShareEvent(locationShareEvent);
//
//        //act
//        try {
//            sut.createTypeHandleAction(request, sessionId,null);
//        }catch (LocationShareException e){
//            actual =e;
//        }
//
//        //assert
//        Assertions.assertThat(actual).isNotNull();
//        Assertions.assertThat(actual.getResponseStatus().getCode()).isEqualTo(3103);
//    }

    @DisplayName("sut는 성공적으로 locationShareEvent 생성한다")
    @Test
    @Transactional
    public void successCreateTypeHandleAction(){

        //arrange
        String identity = Utils.randomMemberId();
        String password = "1234";
        String sessionId = String.valueOf(UUID.randomUUID());
        Double destinationLat = 45.2;
        Double destinationLng = 77.7;
        String destinationName = "산하네집";
        Date date = new Date();
        LocalDateTime localDateTime = date.toInstant()
                .atZone(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime()
                .plusHours(1);

        String encounterDate = convertLocalDateTimeToString(localDateTime);
        LocationShareEvent actual = null;

        //member 가입
        MemberSignUpRequestDto memberSignUpRequestDto = new MemberSignUpRequestDto(identity, password);
        memberService.save(memberSignUpRequestDto);

        Member member = memberRepository.findByIdentityAndIsActiveTrue(identity).get();

        //room create
        RoomCreateRequestDto roomCreateRequestDto = new RoomCreateRequestDto(
                destinationLat,
                destinationLng,
                destinationName,
                encounterDate
        );
        roomService.createRoom(
                roomCreateRequestDto,
                member.getSeq()
        );


        WebSocketAuth webSocketAuth = WebSocketAuth.create(member.getSeq(),sessionId);
        webSocketAuthRepository.save(webSocketAuth);

        int type =0;
        Double presentLat = 12.1;
        Double presentLng = 46.2;
        Double destinationDistance = 41.4;
        LocationShareEventRequestDto request =
                new LocationShareEventRequestDto(type,presentLat,presentLng,destinationDistance);

 LocationShareService sut = new LocationShareService(webSocketAuthRepository,memberRepository, new FileFactoryStub(),locationShareEventRepository);

        //act
        sut.createTypeHandleAction(request, sessionId,null);
        actual = locationShareEventRepository.getByRoomSeq(member.getRoom().getSeq());

        //assert
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getRoomSeq()).isEqualTo(member.getRoom().getSeq());
        Assertions.assertThat(actual.getMemberLocations().size()).isEqualTo(1);
        Assertions.assertThat(actual.getMemberLocations().get(0).getMemberSeq()).isEqualTo(member.getSeq());

    }

    @Transactional
    @DisplayName("sut는 join시 roomSeq로 만들어진 locationShareEvent가 없으면 locationShareEvent를 생성한다")
    @Test
    public void notFoundRoomSeqJoinTest(){
        //arrange
 LocationShareService sut = new LocationShareService(webSocketAuthRepository,memberRepository, new FileFactoryStub(),locationShareEventRepository);
        LocationShareEvent actual = null;

        //방장 member 추가
        String createIdentity = Utils.randomMemberId();
        String createPassword = "1234";
        String createSessionId = String.valueOf(UUID.randomUUID());

        MemberSignUpRequestDto createMemberRequest = new MemberSignUpRequestDto(createIdentity, createPassword);
        memberService.save(createMemberRequest);

        String joinIdentity = Utils.randomMemberId();
        String joinPassword = "1234";
        String joinSessionId = String.valueOf(UUID.randomUUID());

        //join member 가입
        MemberSignUpRequestDto joinMemberRequest = new MemberSignUpRequestDto(joinIdentity, joinPassword);
        memberService.save(joinMemberRequest);

        Member createMember = memberRepository.findByIdentityAndIsActiveTrue(createIdentity).get();
        Member joinMember = memberRepository.findByIdentityAndIsActiveTrue(joinIdentity).get();

        //roomMember -> room create
        Double destinationLat = 45.2;
        Double destinationLng = 77.7;
        String destinationName = "산하네집";
        Date date = new Date();
        LocalDateTime localDateTime = date.toInstant()
                .atZone(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime()
                .plusHours(1);

        String encounterDate = convertLocalDateTimeToString(localDateTime);
        RoomCreateRequestDto roomCreateRequestDto = new RoomCreateRequestDto(
                destinationLat,
                destinationLng,
                destinationName,
                encounterDate
        );
        roomService.createRoom(
                roomCreateRequestDto,
                createMember.getSeq()
        );

        //join Member -> room create
        roomService.createRoom(
                roomCreateRequestDto,
                joinMember.getSeq()
        );

        sut.saveWebSocketAuth(createSessionId,createMember.getSeq());
        sut.saveWebSocketAuth(joinSessionId,joinMember.getSeq());

        //roomMember -> create locationShareEvent
        int createType =0;
        Double presentLat = 12.3;
        Double presentLng = 46.2;
        Double destinationDistance = 41.4;
        LocationShareEventRequestDto createLocationShareEventRequest =
                new LocationShareEventRequestDto(createType,presentLat,presentLng,destinationDistance);

        sut.createTypeHandleAction(createLocationShareEventRequest,createSessionId,null);

        //act
        int joinType =0;
        LocationShareEventRequestDto joinLocationShareEventRequest =
                new LocationShareEventRequestDto(joinType,presentLat,presentLng,destinationDistance);
        sut.joinTypeHandleAction(joinLocationShareEventRequest, joinSessionId,null);

        actual = locationShareEventRepository.getByRoomSeq(joinMember.getRoom().getSeq());

        //assert
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getRoomSeq()).isEqualTo(joinMember.getRoom().getSeq());
    }

    @DisplayName("sut는 성공적으로 locationShareEvent에 join 한다")
    @Transactional
    @Test
    public void successJoinTest(){

        //arrange
 LocationShareService sut = new LocationShareService(webSocketAuthRepository,memberRepository, new FileFactoryStub(),locationShareEventRepository);

        //방장 member 추가
        String createIdentity = Utils.randomMemberId();
        String createPassword = "1234";
        String createSessionId = String.valueOf(UUID.randomUUID());

        MemberSignUpRequestDto createMemberRequest = new MemberSignUpRequestDto(createIdentity, createPassword);
        memberService.save(createMemberRequest);

        String joinIdentity = Utils.randomMemberId();
        String joinPassword = "1234";
        String joinSessionId = String.valueOf(UUID.randomUUID());

        //join member 가입
        MemberSignUpRequestDto joinMemberRequest = new MemberSignUpRequestDto(joinIdentity, joinPassword);
        memberService.save(joinMemberRequest);

        Member createMember = memberRepository.findByIdentityAndIsActiveTrue(createIdentity).get();
        Member joinMember = memberRepository.findByIdentityAndIsActiveTrue(joinIdentity).get();

        //roomMember -> room create
        Double destinationLat = 45.2;
        Double destinationLng = 77.7;
        String destinationName = "산하네집";
        Date date = new Date();
        LocalDateTime localDateTime = date.toInstant()
                .atZone(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime()
                .plusHours(1);

        String encounterDate = convertLocalDateTimeToString(localDateTime);
        RoomCreateRequestDto roomCreateRequestDto = new RoomCreateRequestDto(
                destinationLat,
                destinationLng,
                destinationName,
                encounterDate
        );
        roomService.createRoom(
                roomCreateRequestDto,
                createMember.getSeq()
        );


        //join Member -> room join
        String shareCode = createMember.getRoom().getShareCode();
        JoinRoomRequestDto joinRoomRequestDto = new JoinRoomRequestDto(shareCode);
        roomService.joinRoom(joinRoomRequestDto,joinMember.getSeq());

        sut.saveWebSocketAuth(createSessionId,createMember.getSeq());
        sut.saveWebSocketAuth(joinSessionId,joinMember.getSeq());

        //roomMember -> create locationShareEvent
        int createType =0;
        Double presentLat = 12.3;
        Double presentLng = 46.2;
        Double destinationDistance = 41.4;
        LocationShareEventRequestDto createLocationShareEventRequest =
                new LocationShareEventRequestDto(createType,presentLat,presentLng,destinationDistance);

        sut.createTypeHandleAction(createLocationShareEventRequest,createSessionId,null);

        //act
        int joinType =1;
        LocationShareEventRequestDto joinLocationShareEventRequest =
                new LocationShareEventRequestDto(joinType,presentLat,presentLng,destinationDistance);
        sut.joinTypeHandleAction(joinLocationShareEventRequest,joinSessionId,null);

        //assert
        LocationShareEvent locationShareEventByRoomSeq = locationShareEventRepository.getByRoomSeq(joinMember.getRoom().getSeq());
        List<LocationShareEvent.MemberLocation> memberLocations = locationShareEventByRoomSeq.getMemberLocations();

        Optional<LocationShareEvent.MemberLocation> findJoinMember = memberLocations.stream().filter(
                memberLocation -> memberLocation.getMemberSeq() == joinMember.getSeq()
        ).findFirst();

        Optional<LocationShareEvent.MemberLocation> findCreateMember = memberLocations.stream().filter(
                memberLocation -> memberLocation.getMemberSeq() == createMember.getSeq()
        ).findFirst();



        Assertions.assertThat(findJoinMember.isPresent()).isTrue();
        Assertions.assertThat(findJoinMember).isNotNull();
        Assertions.assertThat(findJoinMember.get().getMemberSeq()).isEqualTo(joinMember.getSeq());
        Assertions.assertThat(findJoinMember.get().getSessionId()).isEqualTo(joinSessionId);

        Assertions.assertThat(findCreateMember.isPresent()).isTrue();
        Assertions.assertThat(findCreateMember.get().getMemberSeq()).isEqualTo(createMember.getSeq());
        Assertions.assertThat(findCreateMember.get().getSessionId()).isEqualTo(createSessionId);
    }

    @Test
    @DisplayName("sut는 접속한 memeber의 distance가 변경되면 locationShareEvent에 memberLocations 값이 변경된다")
    @Transactional
    public void distanceChangeTest(){

        //arrange
 LocationShareService sut = new LocationShareService(webSocketAuthRepository,memberRepository, new FileFactoryStub(),locationShareEventRepository);

        //방장 member 추가
        String createIdentity = Utils.randomMemberId();
        String createPassword = "1234";
        String createSessionId = String.valueOf(UUID.randomUUID());

        MemberSignUpRequestDto createMemberRequest = new MemberSignUpRequestDto(createIdentity, createPassword);
        memberService.save(createMemberRequest);

        String joinIdentity = Utils.randomMemberId();
        String joinPassword = "1234";
        String joinSessionId = String.valueOf(UUID.randomUUID());

        //join member 가입
        MemberSignUpRequestDto joinMemberRequest = new MemberSignUpRequestDto(joinIdentity, joinPassword);
        memberService.save(joinMemberRequest);

        Member createMember = memberRepository.findByIdentityAndIsActiveTrue(createIdentity).get();
        Member joinMember = memberRepository.findByIdentityAndIsActiveTrue(joinIdentity).get();

        //roomMember -> room create
        Double destinationLat = 45.2;
        Double destinationLng = 77.7;
        String destinationName = "산하네집";
        Date date = new Date();
        LocalDateTime localDateTime = date.toInstant()
                .atZone(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime()
                .plusHours(1);

        String encounterDate = convertLocalDateTimeToString(localDateTime);
        RoomCreateRequestDto roomCreateRequestDto = new RoomCreateRequestDto(
                destinationLat,
                destinationLng,
                destinationName,
                encounterDate
        );
        roomService.createRoom(
                roomCreateRequestDto,
                createMember.getSeq()
        );


        //join Member -> room join
        String shareCode = createMember.getRoom().getShareCode();
        JoinRoomRequestDto joinRoomRequestDto = new JoinRoomRequestDto(shareCode);
        roomService.joinRoom(joinRoomRequestDto,joinMember.getSeq());

        sut.saveWebSocketAuth(createSessionId,createMember.getSeq());
        sut.saveWebSocketAuth(joinSessionId,joinMember.getSeq());

        //roomMember -> create locationShareEvent
        int createType =0;
        Double presentLat = 12.3;
        Double presentLng = 46.2;
        Double destinationDistance = 41.4;
        LocationShareEventRequestDto createLocationShareEventRequest =
                new LocationShareEventRequestDto(createType,presentLat,presentLng,destinationDistance);

        sut.createTypeHandleAction(createLocationShareEventRequest,createSessionId,null);

        int joinType =1;
        LocationShareEventRequestDto joinLocationShareEventRequest =
                new LocationShareEventRequestDto(joinType,presentLat,presentLng,destinationDistance);
        sut.joinTypeHandleAction(joinLocationShareEventRequest,joinSessionId,null);

        //act
        int distanceChangeType = 2;
        Double distanceChangePresentLat = 1.1;
        Double distanceChangePresentLng = 1.2;
        Double distanceChangeDestinationDistance = 20.1;
        LocationShareEventRequestDto distanceChangeLocationShareEventRequest =
                new LocationShareEventRequestDto(distanceChangeType,distanceChangePresentLat,distanceChangePresentLng,distanceChangeDestinationDistance);
        sut.distanceChangeHandleAction(distanceChangeLocationShareEventRequest,createSessionId,null);
        LocationShareEvent actual = locationShareEventRepository.findByRoomSeq(createMember.getRoom().getSeq()).get();

        //assert
        Assertions.assertThat(actual.getMemberLocations().size()).isEqualTo(2);
        List<LocationShareEvent.MemberLocation> memberLocations = actual.getMemberLocations();
        Optional<LocationShareEvent.MemberLocation> actualMember = memberLocations.stream().filter(memberLocation -> memberLocation.getMemberSeq() == createMember.getSeq()).findFirst();
        Assertions.assertThat(actualMember.get().getPresentLat()).isEqualTo(distanceChangePresentLat);
        Assertions.assertThat(actualMember.get().getPresentLng()).isEqualTo(distanceChangePresentLng);
        Assertions.assertThat(actualMember.get().getDestinationDistance()).isEqualTo(distanceChangeDestinationDistance);
    }

    @Test
    @DisplayName("sut는 webSocketAuth 영속화에 실패하면 롤백된다")
    @Transactional
    public void saveWebSocketRollBackTest(){
        //arrange
 LocationShareService sut = new LocationShareService(webSocketAuthRepository,memberRepository, new FileFactoryStub(),locationShareEventRepository);
        Member member = createMember();
        Date date = new Date();
        LocalDateTime localDateTime = date.toInstant()
                .atZone(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime()
                .plusHours(1);
        String encounterDate = convertLocalDateTimeToString(localDateTime);
        Room room = Room.create(37.7,24.4,"목적지",encounterDate,member);
        roomRepository.save(room);
        member.setRoom(room);

        String sessionId = String.valueOf(UUID.randomUUID());
        WebSocketAuth webSocketAuth = WebSocketAuth.create(member.getSeq(),sessionId);
        webSocketAuthRepository.save(webSocketAuth);
        WebSocketAuthException actualException = null;

        //act
        String newSessionId = null;
        try {
            sut.saveWebSocketAuth(newSessionId, member.getSeq());
        }catch (WebSocketAuthException e){
            actualException = e;
        }
        Optional<WebSocketAuth> actual = webSocketAuthRepository.findMemberSeq(member.getSeq());

        //assert
        Assertions.assertThat(actual.isPresent()).isEqualTo(true);
        Assertions.assertThat(actualException).isInstanceOf(WebSocketAuthException.class);
    }

    private Member createMember() {
        String id = Utils.randomMemberId();
        String password = "12341234";
        Member member = Member.create(id,password);
        memberRepository.save(member);
        return memberRepository.findByIdentityAndIsActiveTrue(id).get();
    }
}