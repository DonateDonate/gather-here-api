package gather.here.api.infra.socket;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gather.here.api.application.dto.request.LocationShareEventRequestDto;
import gather.here.api.application.dto.response.GetLocationShareResponseDto;
import gather.here.api.application.service.LocationShareService;
import gather.here.api.application.service.TokenService;
import gather.here.api.domain.security.CustomPrincipal;
import gather.here.api.global.exception.ResponseStatus;
import gather.here.api.global.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class CustomWebSocketHandler extends TextWebSocketHandler {
    private final TokenService tokenService;
    private final LocationShareService locationShareService;

    private final List<WebSocketSession> sessionList = new ArrayList<>();

    // 소켓 연결 확인
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("connection on ID ={}",session.getId());

        List<String> authorization = session.getHandshakeHeaders().get("Authorization");

        if(authorization == null  || authorization.get(authorization.size()-1) == null){
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason(ResponseStatus.EMPTY_ACCESS_TOKEN.getMessage()));
            return;
        }

        try {
            String accessTokenTokenWithPrefix = authorization.get(0);
            Authentication authentication = tokenService.accessTokenValidate(accessTokenTokenWithPrefix);
            CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
            Long memberSeq = principal.getMemberSeq();
            locationShareService.saveWebSocketAuth(session.getId(), memberSeq);
            sessionList.add(session);

        }catch (Exception e){
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason(ResponseStatus.INVALID_ACCESS_TOKEN.getMessage()));
        }
    }

    // 소켓 통신 시 메세지의 전송을 다루는 부분
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("send message {} ",session.getId() );
        ObjectMapper objectMapper = new ObjectMapper();
        String payload = message.getPayload();
        LocationShareEventRequestDto request = null;
        try {
            request = objectMapper.readValue(payload, LocationShareEventRequestDto.class);
        }catch (JsonParseException e){
            //todo error 처리
            session.sendMessage(new TextMessage("잘못된 입력값"));
            return;
        }
        handleLocationShareRequest(session, request);
    }

    // 소켓 종료 확인
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("connection close ={} ",session.getId() );
        sessionList.remove(session.getId());
        locationShareService.removeWebSocketAuth(session.getId());
    }

    private void sendMessage(List<String> sessionIdList, String message) {
        for (String id : sessionIdList) {
            for(WebSocketSession webSocketSession : sessionList){
                if(webSocketSession.getId().equals(id)){
                    try {
                        webSocketSession.sendMessage(new TextMessage(message));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private void handleLocationShareRequest(WebSocketSession session, LocationShareEventRequestDto request) {
        if(request.getType() == 0) {
            locationShareService.createTypeHandleAction(request, session.getId());
        }

        if(request.getType() == 1){
            GetLocationShareResponseDto response = locationShareService.joinTypeHandleAction(request, session.getId());
            sendMessage(response.getSessionIdList(), JsonUtil.convertToJsonString(response.getLocationShareMessage()));
        }

        if(request.getType() ==2){
            GetLocationShareResponseDto response = locationShareService.distanceChangeHandleAction(request, session.getId());
            sendMessage(response.getSessionIdList(), JsonUtil.convertToJsonString(response.getLocationShareMessage()));
        }
    }
}
