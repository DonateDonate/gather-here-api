package gather.here.api.application.service;

import gather.here.api.Utils.Utils;
import gather.here.api.application.dto.request.JoinRoomRequestDto;
import gather.here.api.application.dto.request.LocationShareEventRequestDto;
import gather.here.api.application.dto.request.MemberSignUpRequestDto;
import gather.here.api.application.dto.request.RoomCreateRequestDto;
import gather.here.api.domain.entities.LocationShareEvent;
import gather.here.api.domain.entities.Member;
import gather.here.api.domain.entities.WebSocketAuth;
import gather.here.api.domain.repositories.MemberRepository;
import gather.here.api.domain.repositories.RoomRepository;
import gather.here.api.domain.repositories.WebSocketAuthRepository;
import gather.here.api.global.exception.LocationShareException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
class LocationShareServiceTest {
    @Autowired
    private LocationShareService locationShareService;

    @Autowired
    private WebSocketAuthRepository webSocketAuthRepository;

    @Autowired
    private RoomService roomService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    MemberRepository memberRepository;


    @DisplayName("sut는 중복된 memberSeq가 있으면 예외가 발생한다")
    @Test
    public void duplicateSaveWebSocketAuthTest(){
        //arrange
        String sessionId = String.valueOf(UUID.randomUUID());
        Long memberSeq = Utils.randomMemberSeq();
        LocationShareService sut = locationShareService;

        WebSocketAuth webSocketAuth = WebSocketAuth.create(memberSeq, sessionId);
        webSocketAuthRepository.save(webSocketAuth);
        LocationShareException actual = null;

        //act
        try {
            sut.saveWebSocketAuth(sessionId, memberSeq);
        }catch (LocationShareException e){
            actual = e;
        }

        //assert
        Assertions.assertThat(actual.getResponseStatus().getCode()).isEqualTo(3102);
    }

    @DisplayName("sut는 성공적으로 webSocketAuth를 저장한다")
    @Test
    public void successWebSocketAuth(){
        //arrange
        String sessionId = String.valueOf(UUID.randomUUID());
        Long memberSeq =  Utils.randomMemberSeq();
        LocationShareService sut = locationShareService;
        WebSocketAuth actual = null;

        //act
        sut.saveWebSocketAuth(sessionId, memberSeq);

        //assert
        actual = webSocketAuthRepository.findBySessionId(sessionId);
        Assertions.assertThat(actual.getMemberSeq()).isEqualTo(memberSeq);
        Assertions.assertThat(actual.getSessionId()).isEqualTo(sessionId);
    }

    @DisplayName("sut는 locationShareEvent에 중복된 roomSeq값이 있으면 예외가 발생한다")
    @Test
    public void duplicateCreateTypeHandleActionTest(){
        //arrange
        String identity =  Utils.randomMemberId();
        String password = "1234";
        String sessionId = String.valueOf(UUID.randomUUID());
        Float destinationLat = 45.2F;
        Float destinationLng = 77.7F;
        String destinationName = "산하네집";
        String encounterDate = "2025-08-11 15:33";
        LocationShareException actual = null;

        //member 가입
        MemberSignUpRequestDto memberSignUpRequestDto = new MemberSignUpRequestDto(identity, password);
        memberService.save(memberSignUpRequestDto);


        //room create
        RoomCreateRequestDto roomCreateRequestDto = new RoomCreateRequestDto(
                destinationLat,
                destinationLng,
                destinationName,
                encounterDate
        );
        roomService.createRoom(
            roomCreateRequestDto,
                identity
        );


        Member member = memberRepository.findByIdentity(identity).get();

        locationShareService.saveWebSocketAuth(sessionId,member.getSeq());

        int type =0;
        Float presentLat = 12.3F;
        Float presentLng = 46.2F;
        Float destinationDistance = 41.4F;
        LocationShareEventRequestDto request =
                new LocationShareEventRequestDto(type,presentLat,presentLng,destinationDistance);

        LocationShareService sut = locationShareService;

        LocationShareEvent locationShareEvent
                = new LocationShareEvent().create(
                        member.getRoom().getSeq(),
                        member.getSeq(),
                        sessionId,
                        member.getNickname(),
                        member.getImageKey(),
                        presentLat,
                        presentLng,
                        destinationDistance
        );
        roomRepository.saveLocationShareEvent(locationShareEvent);

        //act
        try {
            sut.createTypeHandleAction(request, sessionId);
        }catch (LocationShareException e){
            actual =e;
        }

        //assert
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getResponseStatus().getCode()).isEqualTo(3103);
    }

    @DisplayName("sut는 성공적으로 locationShareEvent 생성한다")
    @Test
    public void successCreateTypeHandleAction(){

        //arrange
        String identity = Utils.randomMemberId();
        String password = "1234";
        String sessionId = String.valueOf(UUID.randomUUID());
        Float destinationLat = 45.2F;
        Float destinationLng = 77.7F;
        String destinationName = "산하네집";
        String encounterDate = "2025-08-11 15:33";
        LocationShareEvent actual = null;

        //member 가입
        MemberSignUpRequestDto memberSignUpRequestDto = new MemberSignUpRequestDto(identity, password);
        memberService.save(memberSignUpRequestDto);

        //room create
        RoomCreateRequestDto roomCreateRequestDto = new RoomCreateRequestDto(
                destinationLat,
                destinationLng,
                destinationName,
                encounterDate
        );
        roomService.createRoom(
                roomCreateRequestDto,
                identity
        );

        Member member = memberRepository.findByIdentity(identity).get();

        locationShareService.saveWebSocketAuth(sessionId,member.getSeq());

        int type =0;
        Float presentLat = 12.3F;
        Float presentLng = 46.2F;
        Float destinationDistance = 41.4F;
        LocationShareEventRequestDto request =
                new LocationShareEventRequestDto(type,presentLat,presentLng,destinationDistance);

        LocationShareService sut = locationShareService;

        //act
        sut.createTypeHandleAction(request, sessionId);
        actual = roomRepository.findLocationShareEventByRoomSeq(member.getRoom().getSeq()).get();

        //assert
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getRoomSeq()).isEqualTo(member.getRoom().getSeq());
        Assertions.assertThat(actual.getMemberLocations().size()).isEqualTo(1);
        Assertions.assertThat(actual.getMemberLocations().get(0).getMemberSeq()).isEqualTo(member.getSeq());

    }

    @DisplayName("sut는 roomSeq로 만들어진 locationShareEvent가 없으면 예외가 발생한다")
    @Test
    public void notFoundRoomSeqJoinTest(){
        //arrange
        LocationShareException actual = null;
        LocationShareService sut = locationShareService;
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

        //roomMember -> room create
        Float destinationLat = 45.2F;
        Float destinationLng = 77.7F;
        String destinationName = "산하네집";
        String encounterDate = "2025-08-11 15:33";
        RoomCreateRequestDto roomCreateRequestDto = new RoomCreateRequestDto(
                destinationLat,
                destinationLng,
                destinationName,
                encounterDate
        );
        roomService.createRoom(
                roomCreateRequestDto,
                createIdentity
        );

        //join Member -> room create
        roomService.createRoom(
                roomCreateRequestDto,
                joinIdentity
        );

        Member createMember = memberRepository.findByIdentity(createIdentity).get();
        Member joinMember = memberRepository.findByIdentity(joinIdentity).get();
        locationShareService.saveWebSocketAuth(createSessionId,createMember.getSeq());
        locationShareService.saveWebSocketAuth(joinSessionId,joinMember.getSeq());

        //roomMember -> create locationShareEvent
        int createType =0;
        Float presentLat = 12.3F;
        Float presentLng = 46.2F;
        Float destinationDistance = 41.4F;
        LocationShareEventRequestDto createLocationShareEventRequest =
                new LocationShareEventRequestDto(createType,presentLat,presentLng,destinationDistance);

        locationShareService.createTypeHandleAction(createLocationShareEventRequest,createSessionId);

        //act
        int joinType =0;
        LocationShareEventRequestDto joinLocationShareEventRequest =
                new LocationShareEventRequestDto(joinType,presentLat,presentLng,destinationDistance);
        try {
            sut.joinTypeHandleAction(joinLocationShareEventRequest, joinSessionId);
        }catch (LocationShareException e){
            actual = e;
        }

        //assert
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getResponseStatus().getCode()).isEqualTo(3104);
    }

    @DisplayName("sut는 성공적으로 locationShareEvent에 join 한다")
    @Transactional
    @Test
    public void successJoinTest(){

        //arrange
        LocationShareService sut = locationShareService;
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

        //roomMember -> room create
        Float destinationLat = 45.2F;
        Float destinationLng = 77.7F;
        String destinationName = "산하네집";
        String encounterDate = "2025-08-11 15:33";
        RoomCreateRequestDto roomCreateRequestDto = new RoomCreateRequestDto(
                destinationLat,
                destinationLng,
                destinationName,
                encounterDate
        );
        roomService.createRoom(
                roomCreateRequestDto,
                createIdentity
        );

        Member createMember = memberRepository.findByIdentity(createIdentity).get();
        Member joinMember = memberRepository.findByIdentity(joinIdentity).get();

        //join Member -> room join
        String shareCode = createMember.getRoom().getShareCode();
        JoinRoomRequestDto joinRoomRequestDto = new JoinRoomRequestDto(shareCode);
        roomService.joinRoom(joinRoomRequestDto,joinIdentity);

        locationShareService.saveWebSocketAuth(createSessionId,createMember.getSeq());
        locationShareService.saveWebSocketAuth(joinSessionId,joinMember.getSeq());

        //roomMember -> create locationShareEvent
        int createType =0;
        Float presentLat = 12.3F;
        Float presentLng = 46.2F;
        Float destinationDistance = 41.4F;
        LocationShareEventRequestDto createLocationShareEventRequest =
                new LocationShareEventRequestDto(createType,presentLat,presentLng,destinationDistance);

        locationShareService.createTypeHandleAction(createLocationShareEventRequest,createSessionId);

        //act
        int joinType =1;
        LocationShareEventRequestDto joinLocationShareEventRequest =
                new LocationShareEventRequestDto(joinType,presentLat,presentLng,destinationDistance);
        sut.joinTypeHandleAction(joinLocationShareEventRequest,joinSessionId);

        //assert
        Optional<LocationShareEvent> locationShareEventByRoomSeq = roomRepository.findLocationShareEventByRoomSeq(joinMember.getRoom().getSeq());
        Assertions.assertThat(locationShareEventByRoomSeq.isPresent()).isTrue();
        List<LocationShareEvent.MemberLocation> memberLocations = locationShareEventByRoomSeq.get().getMemberLocations();

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
        LocationShareService sut = locationShareService;

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

        //roomMember -> room create
        Float destinationLat = 45.2F;
        Float destinationLng = 77.7F;
        String destinationName = "산하네집";
        String encounterDate = "2025-08-11 15:33";
        RoomCreateRequestDto roomCreateRequestDto = new RoomCreateRequestDto(
                destinationLat,
                destinationLng,
                destinationName,
                encounterDate
        );
        roomService.createRoom(
                roomCreateRequestDto,
                createIdentity
        );

        Member createMember = memberRepository.findByIdentity(createIdentity).get();
        Member joinMember = memberRepository.findByIdentity(joinIdentity).get();

        //join Member -> room join
        String shareCode = createMember.getRoom().getShareCode();
        JoinRoomRequestDto joinRoomRequestDto = new JoinRoomRequestDto(shareCode);
        roomService.joinRoom(joinRoomRequestDto,joinIdentity);

        locationShareService.saveWebSocketAuth(createSessionId,createMember.getSeq());
        locationShareService.saveWebSocketAuth(joinSessionId,joinMember.getSeq());

        //roomMember -> create locationShareEvent
        int createType =0;
        Float presentLat = 12.3F;
        Float presentLng = 46.2F;
        Float destinationDistance = 41.4F;
        LocationShareEventRequestDto createLocationShareEventRequest =
                new LocationShareEventRequestDto(createType,presentLat,presentLng,destinationDistance);

        locationShareService.createTypeHandleAction(createLocationShareEventRequest,createSessionId);

        int joinType =1;
        LocationShareEventRequestDto joinLocationShareEventRequest =
                new LocationShareEventRequestDto(joinType,presentLat,presentLng,destinationDistance);
        locationShareService.joinTypeHandleAction(joinLocationShareEventRequest,joinSessionId);

        //act
        int distanceChangeType = 2;
        Float distanceChangePresentLat = 1F;
        Float distanceChangePresentLng = 1F;
        Float distanceChangeDestinationDistance = 20F;
        LocationShareEventRequestDto distanceChangeLocationShareEventRequest =
                new LocationShareEventRequestDto(distanceChangeType,distanceChangePresentLat,distanceChangePresentLng,distanceChangeDestinationDistance);
        sut.distanceChangeHandleAction(distanceChangeLocationShareEventRequest,createSessionId);
        LocationShareEvent actual = roomRepository.findLocationShareEventByRoomSeq(createMember.getRoom().getSeq()).get();

        //assert
        Assertions.assertThat(actual.getMemberLocations().size()).isEqualTo(2);
        List<LocationShareEvent.MemberLocation> memberLocations = actual.getMemberLocations();
        Optional<LocationShareEvent.MemberLocation> actualMember = memberLocations.stream().filter(memberLocation -> memberLocation.getMemberSeq() == createMember.getSeq()).findFirst();
        Assertions.assertThat(actualMember.get().getPresentLat()).isEqualTo(distanceChangePresentLat);
        Assertions.assertThat(actualMember.get().getPresentLng()).isEqualTo(distanceChangePresentLng);
        Assertions.assertThat(actualMember.get().getDestinationDistance()).isEqualTo(distanceChangeDestinationDistance);
    }

    @Test
    @DisplayName("sut는 금메달 은메달 동메달 순위로 score 들어간다")
    @Transactional
    public void setScoreTest(){

        //arrange
        LocationShareService sut = locationShareService;

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

        //roomMember -> room create
        Float destinationLat = 45.2F;
        Float destinationLng = 77.7F;
        String destinationName = "산하네집";
        String encounterDate = "2025-08-11 15:33";
        RoomCreateRequestDto roomCreateRequestDto = new RoomCreateRequestDto(
                destinationLat,
                destinationLng,
                destinationName,
                encounterDate
        );
        roomService.createRoom(
                roomCreateRequestDto,
                createIdentity
        );

        Member createMember = memberRepository.findByIdentity(createIdentity).get();
        Member joinMember = memberRepository.findByIdentity(joinIdentity).get();

        //join Member -> room join
        String shareCode = createMember.getRoom().getShareCode();
        JoinRoomRequestDto joinRoomRequestDto = new JoinRoomRequestDto(shareCode);
        roomService.joinRoom(joinRoomRequestDto,joinIdentity);

        locationShareService.saveWebSocketAuth(createSessionId,createMember.getSeq());
        locationShareService.saveWebSocketAuth(joinSessionId,joinMember.getSeq());

        //roomMember -> create locationShareEvent
        int createType =0;
        Float presentLat = 12.3F;
        Float presentLng = 46.2F;
        Float destinationDistance = 41.4F;
        LocationShareEventRequestDto createLocationShareEventRequest =
                new LocationShareEventRequestDto(createType,presentLat,presentLng,destinationDistance);

        locationShareService.createTypeHandleAction(createLocationShareEventRequest,createSessionId);

        int joinType =1;
        LocationShareEventRequestDto joinLocationShareEventRequest =
                new LocationShareEventRequestDto(joinType,presentLat,presentLng,destinationDistance);
        locationShareService.joinTypeHandleAction(joinLocationShareEventRequest,joinSessionId);

        //act
        int distanceChangeType = 2;
        Float distanceChangePresentLat = 1F;
        Float distanceChangePresentLng = 1F;
        Float distanceChangeDestinationDistance = 1F;
        LocationShareEventRequestDto distanceChangeLocationShareEventRequest =
                new LocationShareEventRequestDto(distanceChangeType,distanceChangePresentLat,distanceChangePresentLng,distanceChangeDestinationDistance);
        sut.distanceChangeHandleAction(distanceChangeLocationShareEventRequest,createSessionId);

        sut.distanceChangeHandleAction(distanceChangeLocationShareEventRequest,joinSessionId);

        LocationShareEvent actual = roomRepository.findLocationShareEventByRoomSeq(createMember.getRoom().getSeq()).get();

        //assert
        Assertions.assertThat(actual.getScore().getGoldMemberSeq()).isEqualTo(createMember.getSeq());
        Assertions.assertThat(actual.getDestinationMemberList().contains(createMember.getSeq())).isTrue();
        Assertions.assertThat(actual.getScore().getSilverMemberSeq()).isEqualTo(joinMember.getSeq());
        Assertions.assertThat(actual.getDestinationMemberList().contains(joinMember.getSeq())).isTrue();
    }

    @Test
    @DisplayName("sut는 성공적으로 memberSeq가 webSocket, locationShareEvent에 삭제된다")
    @Transactional
    public void successRemoveWebSocketAuthAndLocationShareMember(){
        //arrange
        LocationShareService sut  = locationShareService;
        //createMember
        String identity = Utils.randomMemberId();
        String password = "1234";
        String sessionId = String.valueOf(UUID.randomUUID());

        MemberSignUpRequestDto createMemberRequest = new MemberSignUpRequestDto(identity, password);
        memberService.save(createMemberRequest);


        String joinIdentity = Utils.randomMemberId();
        String joinPassword = "1234";
        String joinSessionId = String.valueOf(UUID.randomUUID());

        //join member 가입
        MemberSignUpRequestDto joinMemberRequest = new MemberSignUpRequestDto(joinIdentity, joinPassword);
        memberService.save(joinMemberRequest);

        //create room
        Float destinationLat = 45.2F;
        Float destinationLng = 77.7F;
        String destinationName = "산하네집";
        String encounterDate = "2025-08-11 15:33";
        RoomCreateRequestDto roomCreateRequestDto = new RoomCreateRequestDto(
                destinationLat,
                destinationLng,
                destinationName,
                encounterDate
        );
        roomService.createRoom(
                roomCreateRequestDto,
                identity
        );



        //create webSocketAuth
        Member member = memberRepository.findByIdentity(identity).get();
        locationShareService.saveWebSocketAuth(sessionId,member.getSeq());
        Member joinMember = memberRepository.findByIdentity(joinIdentity).get();
        locationShareService.saveWebSocketAuth(joinSessionId,joinMember.getSeq());

        String shareCode = member.getRoom().getShareCode();
        JoinRoomRequestDto joinRoomRequestDto = new JoinRoomRequestDto(shareCode);
        roomService.joinRoom(joinRoomRequestDto,joinIdentity);

        //create locationShareEvent
        int createType =0;
        Float presentLat = 12.3F;
        Float presentLng = 46.2F;
        Float destinationDistance = 41.4F;
        LocationShareEventRequestDto createLocationShareEventRequest =
                new LocationShareEventRequestDto(createType,presentLat,presentLng,destinationDistance);


        locationShareService.createTypeHandleAction(createLocationShareEventRequest,sessionId);
        int joinType =1;

        LocationShareEventRequestDto joinLocationShareEventRequest =
                new LocationShareEventRequestDto(joinType,presentLat,presentLng,destinationDistance);
        locationShareService.joinTypeHandleAction(joinLocationShareEventRequest,joinSessionId);


        //act
        sut.removeWebSocketAuthAndLocationShareMember(sessionId);
        LocationShareEvent actualLocationShareEvent = roomRepository.findLocationShareEventByRoomSeq(member.getRoom().getSeq()).get();
        WebSocketAuth actualWebSocketAuth = webSocketAuthRepository.findBySessionId(sessionId);

        //assert
        Assertions.assertThat(actualLocationShareEvent.getMemberLocations().contains(member.getSeq())).isFalse();
        Assertions.assertThat(actualLocationShareEvent.getDestinationMemberList().contains(member.getSeq())).isFalse();
        Assertions.assertThat(actualWebSocketAuth).isNull();
    }
}