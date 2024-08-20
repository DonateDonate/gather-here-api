package gather.here.api.application.service;

import gather.here.api.application.dto.request.LocationShareEventRequestDto;
import gather.here.api.domain.entities.LocationShareEvent;
import gather.here.api.domain.entities.Member;
import gather.here.api.domain.entities.WebSocketAuth;
import gather.here.api.domain.file.FileFactory;
import gather.here.api.domain.repositories.MemberRepository;
import gather.here.api.domain.repositories.RoomRepository;
import gather.here.api.domain.repositories.WebSocketAuthRepository;
import gather.here.api.domain.security.CustomPrincipal;
import gather.here.api.global.exception.MemberException;
import gather.here.api.global.exception.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class LocationShareService {
    private final TokenService tokenService;
    private final WebSocketAuthRepository webSocketAuthRepository;
    private final MemberRepository memberRepository;
    private final FileFactory fileFactory;
    private final RoomRepository roomRepository;

    private final List<WebSocketSession> ssessionList = new ArrayList<>();
    @Transactional
    public void validateAndSaveWebSocketSession(WebSocketSession session, String accessTokenTokenWithPrefix) {
        Authentication authentication = tokenService.accessTokenValidate(accessTokenTokenWithPrefix);
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        Long memberSeq = principal.getMemberSeq();
        String memberSessionId = session.getId();

        //todo redis에 기존에 관리하는 sessionId <-> memberSeq가 있는지 확인하는 로직 필요
        WebSocketAuth webSocketAuth = WebSocketAuth.create(memberSeq, memberSessionId);
        webSocketAuthRepository.save(webSocketAuth);
    }

    @Transactional
    public LocationShareEvent createTypeHandleAction(LocationShareEventRequestDto request, String sessionId){
        WebSocketAuth webSocketAuth = webSocketAuthRepository.findBySessionId(sessionId);

        Long memberSeq = webSocketAuth.getMemberSeq();
        Member member = memberRepository.findById(memberSeq)
                .orElseThrow(() -> new MemberException(ResponseStatus.UNCORRECTED_MEMBER_SEQ, HttpStatus.CONFLICT));

        LocationShareEvent locationShareEvent = LocationShareEvent
                .generateTypeCreate(
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
        return locationShareEvent;
    }

    @Transactional
    public void joinTypeHandleAction(LocationShareEventRequestDto request, WebSocketSession session){
        WebSocketAuth webSocketAuth = webSocketAuthRepository.findBySessionId(session.getId());

        Long memberSeq = webSocketAuth.getMemberSeq();
        Member member = memberRepository.findById(memberSeq)
                .orElseThrow(() -> new MemberException(ResponseStatus.UNCORRECTED_MEMBER_SEQ, HttpStatus.CONFLICT));

        LocationShareEvent locationShareEvent = roomRepository.findLocationShareEventByRoomSeq(member.getRoom().getSeq());

        locationShareEvent.addMemberLocations(
                member.getSeq(),
                session.getId(),
                member.getNickname(),
                fileFactory.getImageUrl(member.getImageKey()),
                request.getPresentLat(),
                request.getPresentLng(),
                request.getDestinationDistance()
        );

        roomRepository.updateLocationShareEvent(locationShareEvent);

        List<String> sessionIdList = locationShareEvent.getSessionIdList();
        for (String id : sessionIdList) {
            for(WebSocketSession webSocketSession : ssessionList){
                if(webSocketSession.getId().equals(id)){
                    try {
                        webSocketSession.sendMessage(new TextMessage(request.toString()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }


}
