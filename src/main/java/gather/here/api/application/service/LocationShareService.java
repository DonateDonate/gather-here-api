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
import gather.here.api.global.exception.MemberException;
import gather.here.api.global.exception.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class LocationShareService {
    private final WebSocketAuthRepository webSocketAuthRepository;
    private final MemberRepository memberRepository;
    private final FileFactory fileFactory;
    private final RoomRepository roomRepository;

    @Transactional
    public void saveWebSocketAuth(String sessionId,Long memberSeq) {
        //todo redis에 기존에 관리하는 sessionId <-> memberSeq가 있는지 확인하는 로직 필요
        WebSocketAuth existWebSocketAuth = webSocketAuthRepository.findByMemberSeq(memberSeq);

        if(existWebSocketAuth != null){
            webSocketAuthRepository.deleteByMemberSeq(memberSeq);
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

        LocationShareEvent locationShareEvent = LocationShareEvent
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
        roomRepository.generateLocationShareEvent(locationShareEvent);
    }


    @Transactional
    public GetLocationShareResponseDto joinTypeHandleAction(LocationShareEventRequestDto request, String sessionId){
        WebSocketAuth webSocketAuth = webSocketAuthRepository.findBySessionId(sessionId);

        Long memberSeq = webSocketAuth.getMemberSeq();
        Member member = memberRepository.findById(memberSeq)
                .orElseThrow(() -> new MemberException(ResponseStatus.UNCORRECTED_MEMBER_SEQ, HttpStatus.CONFLICT));

        LocationShareEvent locationShareEvent = roomRepository.findLocationShareEventByRoomSeq(member.getRoom().getSeq());

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

        Long memberSeq = webSocketAuth.getMemberSeq();
        Member member = memberRepository.findById(memberSeq)
                .orElseThrow(() -> new MemberException(ResponseStatus.UNCORRECTED_MEMBER_SEQ, HttpStatus.CONFLICT));

        LocationShareEvent locationShareEvent = roomRepository.findLocationShareEventByRoomSeq(member.getRoom().getSeq());

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
        updateScoreMessage(request.getDestinationDistance(), locationShareEvent, message, member.getSeq());

        return new GetLocationShareResponseDto(message,locationShareEvent.getSessionIdList());
    }

    @Transactional
    public void removeWebSocketAuth(String sessionId){
        WebSocketAuth webSocketAuth = webSocketAuthRepository.findBySessionId(sessionId);
        webSocketAuthRepository.deleteByMemberSeq(webSocketAuth.getMemberSeq());
    }

    private void updateScoreMessage(Float destinationDistance, LocationShareEvent locationShareEvent, LocationShareMessage message, Long memberSeq) {
        final Float goalStandardDistance = 2F;

        if (destinationDistance <= goalStandardDistance) {
            if (locationShareEvent.getScore().getGoldMemberSeq() != null) {
                message.getScoreRes().setGoldMemberSeq(memberSeq);
            }
            if (locationShareEvent.getScore().getSilverMemberSeq() != null) {
                message.getScoreRes().setSilverMemberSeq(memberSeq);
            }
            if (locationShareEvent.getScore().getBronzeMemberSeq() != null) {
                message.getScoreRes().setBronzeMemberSeq(memberSeq);
            }
        }
    }

}
