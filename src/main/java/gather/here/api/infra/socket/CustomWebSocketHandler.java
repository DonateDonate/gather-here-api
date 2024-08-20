package gather.here.api.infra.socket;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gather.here.api.application.dto.request.LocationShareEventRequestDto;
import gather.here.api.application.service.RoomService;
import gather.here.api.application.service.TokenService;
import gather.here.api.domain.entities.LocationShareEvent;
import gather.here.api.domain.entities.WebSocketAuth;
import gather.here.api.domain.security.CustomPrincipal;
import gather.here.api.global.exception.ResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class CustomWebSocketHandler extends TextWebSocketHandler {
    private final RoomService roomService;
    private final TokenService tokenService;

    // 소켓 연결 확인
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        List<String> authorization = session.getHandshakeHeaders().get("Authorization");
        //token이 없는 경우
        if(authorization == null  || authorization.get(authorization.size()-1) == null){
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason(ResponseStatus.EMPTY_TOKEN.getMessage()));
            return;
        }
        String accessTokenTokenWithPrefix = authorization.get(0);
        try {
            Authentication authentication = tokenService.accessTokenValidate(accessTokenTokenWithPrefix);
            CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
            Long memberSeq = principal.getMemberSeq();
            String memberSessionId = session.getId();

            //redis에 기존에 관리하는 sessionId <-> memberSeq가 있는지 확인하는 로직 필요

            WebSocketAuth webSocketAuth = WebSocketAuth.create(memberSeq, memberSessionId);
            roomService.saveWebSocketAuth(webSocketAuth);

        }catch (Exception e){
            e.printStackTrace();
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason(ResponseStatus.INVALID_TOKEN.getMessage()));
        }

        List<WebSocketAuth> byAllWebSocketAuth = roomService.findByAllWebSocketAuth();
        System.out.println("byAllWebSocketAuth = " + byAllWebSocketAuth);
    }

    // 소켓 통신 시 메세지의 전송을 다루는 부분
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String payload = message.getPayload();
        try {
            LocationShareEventRequestDto request = objectMapper.readValue(payload, LocationShareEventRequestDto.class);

            if(request.getType() == 0) {
                roomService.createTypeHandleAction(request, session.getId());

            }

            if(request.getType() == 1){
                LocationShareEvent locationShareEvent = roomService.joinTypeHandleAction(request, session.getId());
                List<String> sessionIdList = locationShareEvent.getSessionIdList();
                for (String id : sessionIdList) {

                }

            }
        }catch (JsonParseException e){
            session.sendMessage(new TextMessage("잘못된 입력값"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // 소켓 종료 확인
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    }
}
