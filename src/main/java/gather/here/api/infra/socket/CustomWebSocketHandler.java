package gather.here.api.infra.socket;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gather.here.api.application.dto.request.LocationShareEventRequestDto;
import gather.here.api.application.dto.response.GetLocationShareResponseDto;
import gather.here.api.application.dto.response.TokenResponseDto;
import gather.here.api.application.service.LocationShareService;
import gather.here.api.application.service.TokenService;
import gather.here.api.domain.security.CustomPrincipal;
import gather.here.api.global.exception.BusinessException;
import gather.here.api.global.exception.LocationShareException;
import gather.here.api.global.exception.ResponseStatus;
import gather.here.api.global.util.JsonUtil;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${security.jwt.access-token.header.name}")
    private String ACCESS_TOKEN_HEADER_NAME;

    @Value("${security.jwt.refresh-token.header.name}")
    private String REFRESH_TOKEN_HEADER_NAME;

    @Value("${security.jwt.access-token.prefix}")
    private String ACCESS_TOKEN_PREFIX;

    private final List<WebSocketSession> sessionList = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            String accessToken = extractAccessToken(session);
            if (accessToken != null) {
                connectHandle(session, accessToken);
            } else {
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason(String.valueOf(ResponseStatus.EMPTY_ACCESS_TOKEN.getCode())));
            }
        } catch (LocationShareException e) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason(String.valueOf(e.getResponseStatus().getCode())));
        }catch (JwtException e ){
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason(String.valueOf(ResponseStatus.INVALID_ACCESS_TOKEN.getCode())));
        }
    }

    private String extractAccessToken(WebSocketSession session) throws Exception {
        List<String> authorization = session.getHandshakeHeaders().get(ACCESS_TOKEN_HEADER_NAME);
        List<String> refreshToken = session.getHandshakeHeaders().get(REFRESH_TOKEN_HEADER_NAME);

        if (refreshToken != null && !refreshToken.isEmpty()) {
            String token = refreshToken.get(refreshToken.size() - 1);
            if (token != null) {
                TokenResponseDto reissueResponse = tokenService.reissue(token);
                session.sendMessage(new TextMessage(JsonUtil.convertToJsonString(reissueResponse)));
                return  ACCESS_TOKEN_PREFIX+ " " +reissueResponse.getAccessToken();
            }
        } else if (authorization != null && !authorization.isEmpty()) {
            return authorization.get(authorization.size() - 1);
        }
        return null;
    }

    private void connectHandle(WebSocketSession session, String accessTokenTokenWithPrefix) {
        Authentication authentication = tokenService.accessTokenValidate(accessTokenTokenWithPrefix);
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        Long memberSeq = principal.getMemberSeq();
        locationShareService.saveWebSocketAuth(session.getId(), memberSeq);
        sessionList.add(session);
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
            session.sendMessage(new TextMessage(ResponseStatus.INVALID_REQUEST.getMessage()));
            return;
        }

        try {
            handleLocationShareRequest(session, request);
        }catch (BusinessException e){
            session.sendMessage(new TextMessage(e.getMessage()));
        }
    }

    // 소켓 종료 확인
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        int code = status.getCode();

        /**
         * service layer에서 발생하는 code에 따른 분기처리 필요
         */
        sessionList.remove(session.getId());
        locationShareService.removeWebSocketAuthAndLocationShareMember(session.getId());

    }

    private void sendMessage(List<String> sessionIdList, String message) {
        try {
            for (String id : sessionIdList) {
                for (WebSocketSession webSocketSession : sessionList) {
                    if (webSocketSession.getId().equals(id)) {
                        try {
                            webSocketSession.sendMessage(new TextMessage(message));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void handleLocationShareRequest(WebSocketSession session, LocationShareEventRequestDto request) {
        try {
            if (request.getType() == 0) {
                locationShareService.createTypeHandleAction(request, session.getId());
            }

            if (request.getType() == 1) {
                GetLocationShareResponseDto response = locationShareService.joinTypeHandleAction(request, session.getId());
                sendMessage(response.getSessionIdList(), JsonUtil.convertToJsonString(response.getLocationShareMessage()));
            }

            if (request.getType() == 2) {
                GetLocationShareResponseDto response = locationShareService.distanceChangeHandleAction(request, session.getId());
                sendMessage(response.getSessionIdList(), JsonUtil.convertToJsonString(response.getLocationShareMessage()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
