package gather.here.api.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gather.here.api.domain.security.CustomPrincipal;
import gather.here.api.domain.service.LocationShareService;
import gather.here.api.domain.service.TokenService;
import gather.here.api.domain.service.dto.request.LocationShareEventRequestDto;
import gather.here.api.domain.service.dto.response.GetLocationShareResponseDto;
import gather.here.api.domain.service.dto.response.TokenResponseDto;
import gather.here.api.global.exception.AuthException;
import gather.here.api.global.exception.BusinessException;
import gather.here.api.global.exception.LocationShareException;
import gather.here.api.global.exception.ResponseStatus;
import gather.here.api.global.util.JsonUtil;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.springframework.web.socket.CloseStatus.BAD_DATA;

@Slf4j
@RequiredArgsConstructor
public class WebSocketService {
    private final TokenService tokenService;
    private final LocationShareService locationShareService;

    @Value("${security.jwt.access-token.header.name}")
    private String ACCESS_TOKEN_HEADER_NAME;

    @Value("${security.jwt.refresh-token.header.name}")
    private String REFRESH_TOKEN_HEADER_NAME;

    @Value("${security.jwt.access-token.prefix}")
    private String ACCESS_TOKEN_PREFIX;

    @Value("${security.jwt.refresh-token.prefix}")
    private String REFRESH_TOKEN_PREFIX;

    private static final List<WebSocketSession> sessionList = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void connectHandle(WebSocketSession session) throws IOException {
        try {
            String accessTokenTokenWithPrefix = extractAccessToken(session);
            Authentication authentication = tokenService.accessTokenValidate(accessTokenTokenWithPrefix);
            CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
            Long memberSeq = principal.getMemberSeq();
            locationShareService.saveWebSocketAuth(session.getId(), memberSeq);
            sessionList.add(session);

        } catch (BusinessException e) {
            CloseStatus closeStatus = new CloseStatus(BAD_DATA.getCode(),String.valueOf(e.getResponseStatus().getCode()));
            session.close(closeStatus);
        } catch (JwtException e) {
            CloseStatus closeStatus = new CloseStatus(BAD_DATA.getCode(),String.valueOf(ResponseStatus.INVALID_ACCESS_TOKEN.getCode()));
            session.close(closeStatus);
        }
    }

    public void messageHandle(WebSocketSession session, TextMessage message) throws IOException {
        try {
            LocationShareEventRequestDto request = objectMapper.readValue(message.getPayload(), LocationShareEventRequestDto.class);
            handleLocationShareRequest(session, request);
        } catch (BusinessException e){
            session.close(BAD_DATA);
            log.error("BusinessException webSocket request body ={}",e.getMessage());
        } catch (Exception e) {
            session.close(BAD_DATA);
            log.error("invalid webSocket request body ={}",e.getMessage());
        }
    }

    @Transactional
    public void connectClosedHandle(WebSocketSession session, CloseStatus status) {
            sessionList.remove(session);
            //GetLocationShareResponseDto response = locationShareService.disConnectHandleAction(session.getId());
            //sendMessage(response.getSessionIdList(), JsonUtil.convertToJsonString(response.getLocationShareMessage()));
    }

    private String extractAccessToken(WebSocketSession session) {

        List<String> authorization = session.getHandshakeHeaders().get(ACCESS_TOKEN_HEADER_NAME);
        List<String> refreshToken = session.getHandshakeHeaders().get(REFRESH_TOKEN_HEADER_NAME);

        if (refreshToken != null && !refreshToken.isEmpty() && refreshToken.get(refreshToken.size() - 1) != null) {
            String token = refreshToken.get(refreshToken.size() - 1);
            TokenResponseDto reissueResponse = tokenService.reissue(token);
            reissueResponse.addPrefix(ACCESS_TOKEN_PREFIX,REFRESH_TOKEN_PREFIX);
            try {

                session.sendMessage(new TextMessage(JsonUtil.convertToJsonString(reissueResponse)));
                return reissueResponse.getAccessToken();
            } catch (IOException e) {
                throw new LocationShareException(ResponseStatus.INVALID_REQUEST, HttpStatus.FORBIDDEN);
            }
        } else if (authorization != null && !authorization.isEmpty()) {
            return authorization.get(authorization.size() - 1);
        }
        throw new AuthException(ResponseStatus.EMPTY_ACCESS_TOKEN, HttpStatus.UNAUTHORIZED);
    }

    private void handleLocationShareRequest(WebSocketSession session, LocationShareEventRequestDto request) {
        Boolean isOpen = session.isOpen();
        try {
            if (request.getType() == 0) {
                locationShareService.createTypeHandleAction(request, session.getId(),isOpen);
            }

            if (request.getType() == 1) {
                GetLocationShareResponseDto response = locationShareService.joinTypeHandleAction(request, session.getId(),isOpen);
                sendMessage(response.getSessionIdList(), JsonUtil.convertToJsonString(response.getLocationShareMessage()));
            }

            if (request.getType() == 2) {
                GetLocationShareResponseDto response = locationShareService.distanceChangeHandleAction(request, session.getId(),isOpen);
                sendMessage(response.getSessionIdList(), JsonUtil.convertToJsonString(response.getLocationShareMessage()));
            }
        }catch (Exception e){
            log.error("Error handling location share request: {} {} {}", e.getCause(),e.getLocalizedMessage(),e.getMessage());
        }
    }

    private void sendMessage(List<String> sessionIdList, String message) {
        for (String id : sessionIdList) {
            sessionList.stream()
                    .filter(session -> session.getId().equals(id) && session.isOpen())
                    .forEach(session -> {
                        try {
                            session.sendMessage(new TextMessage(message));
                        } catch (Exception e) {
                            log.error("Error sending message to session {}: {}", session.getId(), e.getMessage());}
                    });
        }
    }
}
