package gather.here.api.infra.socket;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gather.here.api.application.dto.request.LocationShareEventRequestDto;
import gather.here.api.application.service.LocationShareService;
import gather.here.api.global.exception.ResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class CustomWebSocketHandler extends TextWebSocketHandler {
    private final LocationShareService locationShareService;

    // 소켓 연결 확인
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        List<String> authorization = session.getHandshakeHeaders().get("Authorization");

        if(authorization == null  || authorization.get(authorization.size()-1) == null){
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason(ResponseStatus.EMPTY_TOKEN.getMessage()));
            return;
        }
        try {
            String accessTokenTokenWithPrefix = authorization.get(0);
            locationShareService.validateAndSaveWebSocketSession(session, accessTokenTokenWithPrefix);

        }catch (Exception e){
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason(ResponseStatus.INVALID_TOKEN.getMessage()));
        }
    }
    // 소켓 통신 시 메세지의 전송을 다루는 부분
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String payload = message.getPayload();
        LocationShareEventRequestDto request = null;
        try {
            request = objectMapper.readValue(payload, LocationShareEventRequestDto.class);
        }catch (JsonParseException e){
            //todo error 처리
            session.sendMessage(new TextMessage("잘못된 입력값"));
        }

        if(request.getType() == 0) {
            locationShareService.createTypeHandleAction(request, session.getId());
        }

        if(request.getType() == 1){
            locationShareService.joinTypeHandleAction(request,session);
        }
    }

    // 소켓 종료 확인
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    }
}
