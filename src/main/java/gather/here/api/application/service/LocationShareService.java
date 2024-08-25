package gather.here.api.application.service;

import gather.here.api.application.dto.request.LocationShareEventRequestDto;
import gather.here.api.application.dto.response.GetLocationShareResponseDto;
import gather.here.api.application.dto.response.LocationShareMessage;
import gather.here.api.domain.entities.LocationShareEvent;
import gather.here.api.domain.entities.Member;
import gather.here.api.domain.entities.WebSocketAuth;
import gather.here.api.domain.file.FileFactory;
import gather.here.api.domain.repositories.MemberRepository;
import gather.here.api.domain.repositories.RoomRepository;
import gather.here.api.domain.repositories.WebSocketAuthRepository;
import gather.here.api.global.exception.LocationShareException;
import gather.here.api.global.exception.MemberException;
import gather.here.api.global.exception.ResponseStatus;
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
    public void saveWebSocketAuth(String sessionId,Long memberSeq) {
        Optional<WebSocketAuth> existWebSocketAuth= webSocketAuthRepository.findByMemberSeq(memberSeq);

        if(existWebSocketAuth.isPresent()){
            throw new LocationShareException(ResponseStatus.DUPLICATE_WEB_SOCKET_AUTH_MEMBER_SEQ,HttpStatus.CONFLICT);
        }

        WebSocketAuth webSocketAuth = WebSocketAuth.create(memberSeq, sessionId);
        webSocketAuthRepository.save(webSocketAuth);
    }

    @Transactional
    public void createTypeHandleAction(LocationShareEventRequestDto request, String sessionId){
        WebSocketAuth webSocketAuth = webSocketAuthRepository.findBySessionId(sessionId);

        Long memberSeq = webSocketAuth.getMemberSeq();
        Member member = memberRepository.findById(memberSeq)
                .orElseThrow(() -> new MemberException(ResponseStatus.UNCORRECTED_MEMBER_SEQ, HttpStatus.CONFLICT));

        Optional<LocationShareEvent> existLocationShareEvent = roomRepository.findLocationShareEventByRoomSeq(member.getRoom().getSeq());

        if(existLocationShareEvent.isPresent()){
            throw new LocationShareException(ResponseStatus.DUPLICATE_LOCATION_SHARE_EVENT_ROOM_SEQ, HttpStatus.CONFLICT);
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
    public GetLocationShareResponseDto joinTypeHandleAction(LocationShareEventRequestDto request, String sessionId){
        WebSocketAuth webSocketAuth = webSocketAuthRepository.findBySessionId(sessionId);

        Long memberSeq = webSocketAuth.getMemberSeq();
        Member member = memberRepository.findById(memberSeq)
                .orElseThrow(() -> new MemberException(ResponseStatus.UNCORRECTED_MEMBER_SEQ, HttpStatus.CONFLICT));

        LocationShareEvent locationShareEvent = roomRepository.findLocationShareEventByRoomSeq(member.getRoom().getSeq())
                .orElseThrow(() -> new LocationShareException(ResponseStatus.NOT_FOUND_LOCATION_SHARE_EVENT_BY_ROOM_SEQ,HttpStatus.CONFLICT));

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

        return new GetLocationShareResponseDto(message,locationShareEvent.getSessionIdList());
    }

    @Transactional
    public GetLocationShareResponseDto distanceChangeHandleAction(LocationShareEventRequestDto request, String sessionId){
        WebSocketAuth webSocketAuth = webSocketAuthRepository.findBySessionId(sessionId);

        if(webSocketAuth == null){
            throw new LocationShareException(ResponseStatus.NOT_FOUND_SESSION_ID, HttpStatus.CONFLICT);
        }

        Long memberSeq = webSocketAuth.getMemberSeq();
        Member member = memberRepository.findById(memberSeq)
                .orElseThrow(() -> new MemberException(ResponseStatus.UNCORRECTED_MEMBER_SEQ, HttpStatus.CONFLICT));

        LocationShareEvent locationShareEvent = roomRepository.findLocationShareEventByRoomSeq(member.getRoom().getSeq())
                .orElseThrow(() -> new LocationShareException(ResponseStatus.NOT_FOUND_ROOM_SEQ ,HttpStatus.CONFLICT));

        if(locationShareEvent.getDestinationMemberList() != null && locationShareEvent.getDestinationMemberList().contains(memberSeq)){
            throw new LocationShareException(ResponseStatus.ALREADY_ARRIVED_MEMBER,HttpStatus.CONFLICT);
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

        return new GetLocationShareResponseDto(message,locationShareEvent.getSessionIdList());
    }

    @Transactional
    public void removeWebSocketAuthAndLocationShareMember(String sessionId){
        //todo 동시성 제어
        // todo 메시지를 보낼꺼 ? -> 재실행 처리 어떤식으로 ? ex) session이 살아있는지 확인하는 api를 만들어서 처리?
        WebSocketAuth webSocketAuth = webSocketAuthRepository.findBySessionId(sessionId);

        if(webSocketAuth == null){
            throw new LocationShareException(ResponseStatus.NOT_FOUND_SESSION_ID, HttpStatus.CONFLICT);
        }

        webSocketAuthRepository.deleteByMemberSeq(webSocketAuth.getMemberSeq());

        Member member = memberRepository.findById(webSocketAuth.getMemberSeq())
                .orElseThrow(()-> new MemberException(ResponseStatus.UNCORRECTED_MEMBER_SEQ, HttpStatus.CONFLICT));

        LocationShareEvent locationShareEvent = roomRepository.findLocationShareEventByRoomSeq(member.getRoom().getSeq())
                .orElseThrow(()-> new LocationShareException(ResponseStatus.NOT_FOUND_ROOM_SEQ, HttpStatus.CONFLICT));

        locationShareEvent.removeMemberLocation(member.getSeq());
        locationShareEvent.removeDestinationMemberList(member.getSeq());
        roomRepository.updateLocationShareEvent(locationShareEvent);
    }

    private void updateDestinationMember(Float destinationDistance, LocationShareEvent locationShareEvent,Long memberSeq) {
        final Float goalStandardDistance = 2F;
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
