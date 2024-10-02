package gather.here.api.domain.service;

import gather.here.api.domain.entities.LocationShareEvent;
import gather.here.api.domain.entities.Member;
import gather.here.api.domain.entities.WebSocketAuth;
import gather.here.api.domain.file.FileFactory;
import gather.here.api.domain.repositories.MemberRepository;
import gather.here.api.domain.repositories.RoomRepository;
import gather.here.api.domain.repositories.WebSocketAuthRepository;
import gather.here.api.domain.service.dto.request.LocationShareEventRequestDto;
import gather.here.api.domain.service.dto.response.GetLocationShareResponseDto;
import gather.here.api.domain.service.dto.response.LocationShareMessage;
import gather.here.api.global.exception.LocationShareException;
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
    private final RoomRepository roomRepository;

    @Transactional
    public void saveWebSocketAuth(String sessionId, Long memberSeq) {
        log.info("save sessionId ={}",sessionId);
        Member member = memberRepository.getBySeq(memberSeq);
        if(member.getRoom() == null || member.getRoom().getStatus() == 9){
            throw new RoomException(ResponseStatus.CLOSED_ROOM, HttpStatus.FORBIDDEN);
        }
        Optional<WebSocketAuth> existWebSocketAuth = webSocketAuthRepository.findMemberSeq(memberSeq);
        if(existWebSocketAuth.isPresent()){
            webSocketAuthRepository.deleteByMemberSeq(memberSeq);
            updateLocationShareEventByMemberSeq(member);
        }
        WebSocketAuth webSocketAuth = WebSocketAuth.create(memberSeq, sessionId);
        webSocketAuthRepository.save(webSocketAuth);
    }

    @Transactional
    public void createTypeHandleAction(LocationShareEventRequestDto request, String sessionId,Boolean isOpen) {
       log.info("create sessionId = {} ",sessionId);
        WebSocketAuth webSocketAuth = webSocketAuthRepository.getBySessionId(sessionId);

        Long memberSeq = webSocketAuth.getMemberSeq();
        Member member = memberRepository.getBySeq(memberSeq);
        Optional<LocationShareEvent> existLocationShareEvent = roomRepository.findLocationShareEventByRoomSeq(member.getRoom().getSeq());

        if (existLocationShareEvent.isPresent()) {
            existLocationShareEvent.get().removeMemberLocation(memberSeq);
        }

        LocationShareEvent locationShareEvent = new LocationShareEvent()
                .create(
                        member.getRoom().getSeq(),
                        member.getSeq(),
                        sessionId,
                        member.getNickname(),
                        fileFactory.getImageUrl(member.getImageKey()),
                        request.getPresentLat(),
                        request.getPresentLng(),
                        request.getDestinationDistance(),
                        isOpen
                );
        roomRepository.saveLocationShareEvent(locationShareEvent);
    }

    @Transactional
    public GetLocationShareResponseDto joinTypeHandleAction(LocationShareEventRequestDto request, String sessionId,Boolean isOpen) {
        log.info("join sessionId = {}",sessionId);
        WebSocketAuth webSocketAuth = webSocketAuthRepository.getBySessionId(sessionId);

        Long memberSeq = webSocketAuth.getMemberSeq();
        Member member = memberRepository.getBySeq(memberSeq);
        Optional<LocationShareEvent> locationShareEventOptional = roomRepository.findLocationShareEventByRoomSeq(member.getRoom().getSeq());
        LocationShareEvent locationShareEvent = null;
        if(locationShareEventOptional.isPresent()){
            locationShareEvent = locationShareEventOptional.get();
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
            roomRepository.updateLocationShareEvent(locationShareEvent);
        }else{
            locationShareEvent = new LocationShareEvent()
                    .create(
                            member.getRoom().getSeq(),
                            member.getSeq(),
                            sessionId,
                            member.getNickname(),
                            fileFactory.getImageUrl(member.getImageKey()),
                            request.getPresentLat(),
                            request.getPresentLng(),
                            request.getDestinationDistance(),
                            isOpen
                    );
            roomRepository.saveLocationShareEvent(locationShareEvent);
        }
        LocationShareMessage message = LocationShareMessage.from(locationShareEvent);

        return new GetLocationShareResponseDto(message, locationShareEvent.getSessionIdList());
    }

    @Transactional
    public GetLocationShareResponseDto distanceChangeHandleAction(LocationShareEventRequestDto request, String sessionId, Boolean isOpen) {
        log.info("distance change sessionId = {} ",sessionId);
        WebSocketAuth webSocketAuth = webSocketAuthRepository.getBySessionId(sessionId);
        Long memberSeq = webSocketAuth.getMemberSeq();
        Member member = memberRepository.getBySeq(memberSeq);
        LocationShareEvent locationShareEvent = roomRepository.getLocationShareEventByRoomSeq(member.getRoom().getSeq());

        validateAlreadyArriveMember(locationShareEvent, memberSeq);
        updateMemberLocation(request, sessionId, locationShareEvent, member,isOpen);

        LocationShareMessage message = LocationShareMessage.from(locationShareEvent);
        updateDestinationMember(request.getDestinationDistance(), locationShareEvent, member.getSeq());
        roomRepository.updateLocationShareEvent(locationShareEvent);

        return new GetLocationShareResponseDto(message, locationShareEvent.getSessionIdList());
    }

    @Transactional
    public GetLocationShareResponseDto disConnectHandleAction(String sessionId) {
        log.info("remove sessionId = {}", sessionId);
        WebSocketAuth webSocketAuth = webSocketAuthRepository.getBySessionId(sessionId);
        Member member = memberRepository.getBySeq(webSocketAuth.getMemberSeq());
        LocationShareEvent locationShareEvent = roomRepository.getLocationShareEventByRoomSeq(member.getRoom().getSeq());
        LocationShareMessage message = LocationShareMessage.from(locationShareEvent);
        webSocketAuthRepository.deleteByMemberSeq(member.getSeq());
        return new GetLocationShareResponseDto(message, locationShareEvent.getSessionIdList());
    }

    private void updateDestinationMember(Double destinationDistance, LocationShareEvent locationShareEvent,Long memberSeq) {
        final Double goalStandardDistance = 10.0;
        if (destinationDistance <= goalStandardDistance ) {
            locationShareEvent.addDestinationMemberList(memberSeq);
            if (locationShareEvent.getScore() == null || locationShareEvent.getScore().getGoldMemberSeq() == null) {
                locationShareEvent.setGoldMemberSeq(memberSeq);
                return;
            }
            if (locationShareEvent.getScore().getSilverMemberSeq() == null && !locationShareEvent.getScoreList().contains(memberSeq)) {
                locationShareEvent.setSilverMemberSeq(memberSeq);
                return;
            }
            if (locationShareEvent.getScore().getBronzeMemberSeq() == null && !locationShareEvent.getScoreList().contains(memberSeq)) {
                locationShareEvent.setBronzeMemberSeq(memberSeq);
            }
        }
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

    private void validateAlreadyArriveMember(LocationShareEvent locationShareEvent, Long memberSeq) {
        if (locationShareEvent.getDestinationMemberList() != null && locationShareEvent.getDestinationMemberList().contains(memberSeq)) {
            throw new LocationShareException(ResponseStatus.ALREADY_ARRIVED_MEMBER, HttpStatus.FORBIDDEN);
        }
    }

    private void updateLocationShareEventByMemberSeq(Member member) {
        LocationShareEvent locationShareEvent = roomRepository.getLocationShareEventByRoomSeq(member.getRoom().getSeq());
        locationShareEvent.removeMemberLocation(member.getSeq());
        locationShareEvent.removeDestinationMemberList(member.getSeq());
        roomRepository.updateLocationShareEvent(locationShareEvent);
    }
}
