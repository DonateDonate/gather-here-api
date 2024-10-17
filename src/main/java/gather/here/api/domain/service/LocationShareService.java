package gather.here.api.domain.service;

import gather.here.api.domain.entities.LocationShareEvent;
import gather.here.api.domain.entities.Member;
import gather.here.api.domain.entities.WebSocketAuth;
import gather.here.api.domain.etc.TransactionManager;
import gather.here.api.domain.file.FileFactory;
import gather.here.api.domain.repositories.LocationShareEventRepository;
import gather.here.api.domain.repositories.MemberRepository;
import gather.here.api.domain.repositories.WebSocketAuthRepository;
import gather.here.api.domain.service.dto.request.LocationShareEventRequestDto;
import gather.here.api.domain.service.dto.response.GetLocationShareResponseDto;
import gather.here.api.domain.service.dto.response.LocationShareMessage;
import gather.here.api.global.exception.ResponseStatus;
import gather.here.api.global.exception.RoomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RequiredArgsConstructor
public class LocationShareService {
    private final WebSocketAuthRepository webSocketAuthRepository;
    private final MemberRepository memberRepository;
    private final FileFactory fileFactory;
    private final LocationShareEventRepository locationShareEventRepository;
    private final TransactionManager transactionManager;

    @Transactional
    public void saveWebSocketAuth(String sessionId, Long memberSeq) {
        Member member = memberRepository.getBySeq(memberSeq);
        if (member.getRoom() == null || member.getRoom().getStatus() == 9) {
            throw new RoomException(ResponseStatus.CLOSED_ROOM, HttpStatus.FORBIDDEN);
        }
        transactionManager.transaction(() -> {
            deleteMemberIfExist(member);
            WebSocketAuth webSocketAuth = WebSocketAuth.create(memberSeq, sessionId);
            webSocketAuthRepository.save(webSocketAuth);
        });
        logGenerater(sessionId,"웹 소켓 연결");
    }

    @Transactional
    public void createTypeHandleAction(LocationShareEventRequestDto request, String sessionId) {
        transactionManager.transaction(()->{
            updateLocationShareEvent(request, sessionId);
        });
        logGenerater(sessionId,"event 생성");
    }

    @Transactional
    public GetLocationShareResponseDto joinTypeHandleAction(LocationShareEventRequestDto request, String sessionId) {
        AtomicReference<LocationShareEvent> locationShareEventRef = new AtomicReference<>();
        AtomicReference<LocationShareMessage> messageRef = new AtomicReference<>();

        transactionManager.transaction(() -> {
            LocationShareEvent locationShareEvent = updateLocationShareEvent(request, sessionId);  // LocationShareEvent 업데이트
            LocationShareMessage message = LocationShareMessage.from(locationShareEvent);  // LocationShareMessage 생성

            locationShareEventRef.set(locationShareEvent);
            messageRef.set(message);
        });

        LocationShareEvent locationShareEvent = locationShareEventRef.get();
        LocationShareMessage message = messageRef.get();
        List<String> sessionIdList = locationShareEvent.getSessionIdList();
        logGenerater(sessionId,"event 참가");
        return new GetLocationShareResponseDto(message, sessionIdList);
    }

    @Transactional
    public GetLocationShareResponseDto distanceChangeHandleAction(LocationShareEventRequestDto request, String sessionId) {
        AtomicReference<LocationShareEvent> locationShareEventRef = new AtomicReference<>();
        AtomicReference<LocationShareMessage> messageRef = new AtomicReference<>();

        transactionManager.transaction(()->{
            Member member = getMemberByWebSocketAuth(sessionId);
            LocationShareEvent locationShareEvent = locationShareEventRepository.getByRoomSeq(member.getRoom().getSeq());
            updateMemberLocation(request, sessionId, locationShareEvent, member);
            LocationShareMessage message = LocationShareMessage.from(locationShareEvent);
            locationShareEventRepository.update(locationShareEvent);

            locationShareEventRef.set(locationShareEvent);
            messageRef.set(message);
        });
        LocationShareEvent locationShareEvent = locationShareEventRef.get();
        List<String> sessionIdList = locationShareEvent.getSessionIdList();
        LocationShareMessage message = messageRef.get();
        logGenerater(sessionId,"distance 변경");
        return new GetLocationShareResponseDto(message, sessionIdList);
    }

    private Member getMemberByWebSocketAuth(String sessionId) {
        WebSocketAuth webSocketAuth = webSocketAuthRepository.getBySessionId(sessionId);
        Long memberSeq = webSocketAuth.getMemberSeq();
        Member member = memberRepository.getBySeq(memberSeq);
        return member;
    }

    @Transactional
    public GetLocationShareResponseDto disConnectHandleAction(String sessionId) {
        WebSocketAuth webSocketAuth = webSocketAuthRepository.getBySessionId(sessionId);
        Member member = memberRepository.getBySeq(webSocketAuth.getMemberSeq());
        /**
         * room service 에서 exit 시키기 때문에 member.getRoom이 없음
         */
        LocationShareEvent locationShareEvent = locationShareEventRepository.getByRoomSeq(member.getRoom().getSeq());
        LocationShareMessage message = LocationShareMessage.from(locationShareEvent);
        webSocketAuthRepository.deleteByMemberSeq(webSocketAuth);
        return new GetLocationShareResponseDto(message, locationShareEvent.getSessionIdList());
    }

    private LocationShareEvent updateLocationShareEvent(LocationShareEventRequestDto request, String sessionId){
        Member member = getMemberByWebSocketAuth(sessionId);
        LocationShareEvent locationShareEvent = locationShareEventRepository.getByRoomSeq(member.getRoom().getSeq());
        locationShareEvent.addMemberLocations(
                member.getSeq(),
                sessionId,
                member.getNickname(),
                fileFactory.getImageUrl(member.getImageKey()),
                request.getPresentLat(),
                request.getPresentLng(),
                request.getDestinationDistance()
        );
        locationShareEventRepository.update(locationShareEvent);
        return locationShareEvent;
    }
    private void deleteMemberIfExist(Member member) {
        Optional<WebSocketAuth> existWebSocketAuth = webSocketAuthRepository.findMemberSeq(member.getSeq());
        if (existWebSocketAuth.isPresent()) {
            webSocketAuthRepository.deleteByMemberSeq(existWebSocketAuth.get());
            Optional<LocationShareEvent> locationShareEvent = locationShareEventRepository.findByRoomSeq(member.getRoom().getSeq());
            if(locationShareEvent.isPresent()){
                locationShareEvent.get().removeMemberLocation(member.getSeq());
                locationShareEventRepository.update(locationShareEvent.get());
            }
        }
    }

    private void updateMemberLocation(LocationShareEventRequestDto request, String sessionId, LocationShareEvent locationShareEvent, Member member) {
        locationShareEvent.getMemberLocations()
                .removeIf(memberLocation -> memberLocation.getSessionId().equals(sessionId));

        locationShareEvent.addMemberLocations(
                member.getSeq(),
                sessionId,
                member.getNickname(),
                fileFactory.getImageUrl(member.getImageKey()),
                request.getPresentLat(),
                request.getPresentLng(),
                request.getDestinationDistance()
        );
    }
    private void logGenerater(String sessionId, String message){
        WebSocketAuth webSocketAuth = webSocketAuthRepository.getBySessionId(sessionId);
        Member member = memberRepository.getBySeq(webSocketAuth.getMemberSeq());
        log.info("{} ({})  ->  {} ",member.getNickname(),sessionId,message);
    }
}
