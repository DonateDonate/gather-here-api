package gather.here.api.domain.service;

import gather.here.api.domain.service.dto.request.LocationShareEventRequestDto;
import gather.here.api.domain.service.dto.response.GetLocationShareResponseDto;
import gather.here.api.domain.service.dto.response.LocationShareMessage;
import gather.here.api.domain.entities.LocationShareEvent;
import gather.here.api.domain.entities.Member;
import gather.here.api.domain.entities.WebSocketAuth;
import gather.here.api.domain.file.FileFactory;
import gather.here.api.domain.repositories.MemberRepository;
import gather.here.api.domain.repositories.RoomRepository;
import gather.here.api.domain.repositories.WebSocketAuthRepository;
import gather.here.api.global.exception.LocationShareException;
import gather.here.api.global.exception.ResponseStatus;
import gather.here.api.global.exception.RoomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
public class LocationShareService {
    private final WebSocketAuthRepository webSocketAuthRepository;
    private final MemberRepository memberRepository;
    private final FileFactory fileFactory;
    private final RoomRepository roomRepository;

    @Transactional
    public void saveWebSocketAuth(String sessionId, Long memberSeq) {
        Member member = memberRepository.getBySeq(memberSeq);
        if(member.getRoom() == null || member.getRoom().getStatus() == 9){
            throw new RoomException(ResponseStatus.CLOSED_ROOM, HttpStatus.FORBIDDEN);
        }
        Optional<WebSocketAuth> existWebSocketAuth = webSocketAuthRepository.findMemberSeq(memberSeq);
        if(existWebSocketAuth.isPresent()){
            webSocketAuthRepository.deleteByMemberSeq(memberSeq);
        }
        WebSocketAuth webSocketAuth = WebSocketAuth.create(memberSeq, sessionId);
        webSocketAuthRepository.save(webSocketAuth);
    }

    @Transactional
    public void createTypeHandleAction(LocationShareEventRequestDto request, String sessionId) {
        WebSocketAuth webSocketAuth = webSocketAuthRepository.getBySessionId(sessionId);

        Long memberSeq = webSocketAuth.getMemberSeq();
        Member member = memberRepository.getBySeq(memberSeq);
        Optional<LocationShareEvent> existLocationShareEvent = roomRepository.findLocationShareEventByRoomSeq(member.getRoom().getSeq());

        if (existLocationShareEvent.isPresent()) {
            throw new LocationShareException(ResponseStatus.DUPLICATE_LOCATION_SHARE_EVENT_ROOM_SEQ, HttpStatus.FORBIDDEN);
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
                        request.getDestinationDistance()
                );
        roomRepository.saveLocationShareEvent(locationShareEvent);
    }

    @Transactional
    public GetLocationShareResponseDto joinTypeHandleAction(LocationShareEventRequestDto request, String sessionId) {
        WebSocketAuth webSocketAuth = webSocketAuthRepository.getBySessionId(sessionId);

        Long memberSeq = webSocketAuth.getMemberSeq();
        Member member = memberRepository.getBySeq(memberSeq);
        LocationShareEvent locationShareEvent = roomRepository.getLocationShareEventByRoomSeq(member.getRoom().getSeq());

        locationShareEvent.addMemberLocations(
                member.getSeq(),
                sessionId,
                member.getNickname(),
                fileFactory.getImageUrl(member.getImageKey()),
                request.getPresentLat(),
                request.getPresentLng(),
                request.getDestinationDistance()
        );

        roomRepository.updateLocationShareEvent(locationShareEvent);

        LocationShareMessage message = LocationShareMessage.from(locationShareEvent);

        return new GetLocationShareResponseDto(message, locationShareEvent.getSessionIdList());
    }

    @Transactional
    public GetLocationShareResponseDto distanceChangeHandleAction(LocationShareEventRequestDto request, String sessionId) {
        WebSocketAuth webSocketAuth = webSocketAuthRepository.getBySessionId(sessionId);
        Long memberSeq = webSocketAuth.getMemberSeq();
        Member member = memberRepository.getBySeq(memberSeq);
        LocationShareEvent locationShareEvent = roomRepository.getLocationShareEventByRoomSeq(member.getRoom().getSeq());
        if (locationShareEvent.getDestinationMemberList() != null && locationShareEvent.getDestinationMemberList().contains(memberSeq)) {
            throw new LocationShareException(ResponseStatus.ALREADY_ARRIVED_MEMBER, HttpStatus.FORBIDDEN);
        }
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

        LocationShareMessage message = LocationShareMessage.from(locationShareEvent);
        updateDestinationMember(request.getDestinationDistance(), locationShareEvent, member.getSeq());
        roomRepository.updateLocationShareEvent(locationShareEvent);

        return new GetLocationShareResponseDto(message, locationShareEvent.getSessionIdList());
    }

    @Transactional
    public void removeWebSocketAuthAndLocationShareMember(String sessionId) {
        WebSocketAuth webSocketAuth = webSocketAuthRepository.getBySessionId(sessionId);
        webSocketAuthRepository.deleteByMemberSeq(webSocketAuth.getMemberSeq());
        Member member = memberRepository.getBySeq(webSocketAuth.getMemberSeq());
        LocationShareEvent locationShareEvent = roomRepository.getLocationShareEventByRoomSeq(member.getRoom().getSeq());

        locationShareEvent.removeMemberLocation(member.getSeq());
        locationShareEvent.removeDestinationMemberList(member.getSeq());
        roomRepository.updateLocationShareEvent(locationShareEvent);
    }

    private void updateDestinationMember(Float destinationDistance, LocationShareEvent locationShareEvent,Long memberSeq) {
        final Float goalStandardDistance = 10F;
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
}
