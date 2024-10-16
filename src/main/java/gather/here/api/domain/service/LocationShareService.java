package gather.here.api.domain.service;

import gather.here.api.domain.entities.LocationShareEvent;
import gather.here.api.domain.entities.Member;
import gather.here.api.domain.entities.WebSocketAuth;
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

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class LocationShareService {
    private final WebSocketAuthRepository webSocketAuthRepository;
    private final MemberRepository memberRepository;
    private final FileFactory fileFactory;
    private final LocationShareEventRepository locationShareEventRepository;

    @Transactional
    public void saveWebSocketAuth(String sessionId, Long memberSeq) {
        Member member = memberRepository.getBySeq(memberSeq);
        if (member.getRoom() == null || member.getRoom().getStatus() == 9) {
            throw new RoomException(ResponseStatus.CLOSED_ROOM, HttpStatus.FORBIDDEN);
        }
        WebSocketAuth webSocketAuth = WebSocketAuth.create(memberSeq, sessionId);
        Optional<WebSocketAuth> existWebSocketAuth = webSocketAuthRepository.findMemberSeq(memberSeq);
        deleteMemberSeqByWebSocketAuthAndLocationShareEventIfExist(existWebSocketAuth, member);
        webSocketAuthRepository.save(webSocketAuth);
        logGenerater(member,"웹 소켓 연결");
    }

    private void deleteMemberSeqByWebSocketAuthAndLocationShareEventIfExist(Optional<WebSocketAuth> existWebSocketAuth, Member member) {
        if (existWebSocketAuth.isPresent()) {
            webSocketAuthRepository.deleteByMemberSeq(existWebSocketAuth.get());
            Optional<LocationShareEvent> locationShareEvent = locationShareEventRepository.findByRoomSeq(member.getRoom().getSeq());
            if(locationShareEvent.isPresent()){
                locationShareEvent.get().removeMemberLocation(member.getSeq());
                locationShareEventRepository.update(locationShareEvent.get());
            }
        }
    }

    @Transactional
    public void createTypeHandleAction(LocationShareEventRequestDto request, String sessionId,Boolean isOpen) {
        Member member = getMemberByWebSocketAuth(sessionId);
        LocationShareEvent locationShareEvent = locationShareEventRepository.getByRoomSeq(member.getRoom().getSeq());
        locationShareEvent.addMemberLocations(
                member.getSeq(),
                sessionId,
                member.getNickname(),
                fileFactory.getImageUrl(member.getImageKey()),
                request.getPresentLat(),
                request.getPresentLng(),
                request.getDestinationDistance(),
                isOpen
        );
        locationShareEventRepository.update(locationShareEvent);
        logGenerater(member,"event 생성");
    }

    @Transactional
    public GetLocationShareResponseDto joinTypeHandleAction(LocationShareEventRequestDto request, String sessionId,Boolean isOpen) {
        Member member = getMemberByWebSocketAuth(sessionId);
        LocationShareEvent locationShareEvent = locationShareEventRepository.getByRoomSeq(member.getRoom().getSeq());
        locationShareEvent.addMemberLocations(
                    member.getSeq(),
                    sessionId,
                    member.getNickname(),
                    fileFactory.getImageUrl(member.getImageKey()),
                    request.getPresentLat(),
                    request.getPresentLng(),
                    request.getDestinationDistance(),
                    isOpen
            );
        locationShareEventRepository.update(locationShareEvent);
        LocationShareMessage message = LocationShareMessage.from(locationShareEvent);
        logGenerater(member,"event 참가");
        return new GetLocationShareResponseDto(message, locationShareEvent.getSessionIdList());
    }

    @Transactional
    public GetLocationShareResponseDto distanceChangeHandleAction(LocationShareEventRequestDto request, String sessionId, Boolean isOpen) {
        Member member = getMemberByWebSocketAuth(sessionId);
        LocationShareEvent locationShareEvent = locationShareEventRepository.getByRoomSeq(member.getRoom().getSeq());
        updateMemberLocation(request, sessionId, locationShareEvent, member,isOpen);
        LocationShareMessage message = LocationShareMessage.from(locationShareEvent);
        locationShareEventRepository.update(locationShareEvent);
        logGenerater(member,"distance 변경");
        return new GetLocationShareResponseDto(message, locationShareEvent.getSessionIdList());
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

    private void updateMemberLocation(LocationShareEventRequestDto request, String sessionId, LocationShareEvent locationShareEvent, Member member, Boolean isOpen) {
        locationShareEvent.getMemberLocations()
                .removeIf(memberLocation -> memberLocation.getSessionId().equals(sessionId));

        locationShareEvent.addMemberLocations(
                member.getSeq(),
                sessionId,
                member.getNickname(),
                fileFactory.getImageUrl(member.getImageKey()),
                request.getPresentLat(),
                request.getPresentLng(),
                request.getDestinationDistance(),
                isOpen
        );
    }

    private Member getMemberByWebSocketAuth(String sessionId) {
        WebSocketAuth webSocketAuth = webSocketAuthRepository.getBySessionId(sessionId);
        Long memberSeq = webSocketAuth.getMemberSeq();
        return memberRepository.getBySeq(memberSeq);
    }

    private void logGenerater(Member member, String message){
        log.info("{}  ->  {} ",member.getNickname(),message);
    }
}
