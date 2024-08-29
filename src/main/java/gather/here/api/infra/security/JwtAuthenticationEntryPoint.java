package gather.here.api.infra.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import gather.here.api.global.exception.BusinessException;
import gather.here.api.global.exception.CustomResponseBody;
import gather.here.api.global.exception.ResponseStatus;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Exception exception = (Exception) request.getAttribute("exception");
        // 토큰 만료의 경우
        if(exception instanceof JwtException) {
            if (exception instanceof ExpiredJwtException) {
                if(request.getAttribute("type").equals("access")){
                    sendErrorResponse(response, ResponseStatus.EXPIRE_ACCESS_TOKEN);
                }else if(request.getAttribute("type").equals("refresh")){
                    sendErrorResponse(response, ResponseStatus.EXPIRE_REFRESH_TOKEN);
                }
                return;
            }

            if (exception instanceof MalformedJwtException) {
                if(request.getAttribute("type").equals("access")){
                    sendErrorResponse(response, ResponseStatus.INVALID_ACCESS_TOKEN);
                }else if(request.getAttribute("type").equals("refresh")){
                    sendErrorResponse(response, ResponseStatus.INVALID_REFRESH_TOKEN);
                }
                return;
            }
        }
        if(exception instanceof BusinessException) {
            sendErrorResponse(response, ((BusinessException) exception).getResponseStatus());
            return;
        }

        sendErrorResponse(response,ResponseStatus.EMPTY_ACCESS_TOKEN);
    }

    private void sendErrorResponse(HttpServletResponse response, ResponseStatus responseStatus)
            throws IOException {
        CustomResponseBody errorResponse = new CustomResponseBody(responseStatus.getMessage(), responseStatus.getCode());

        String body = objectMapper.writeValueAsString(errorResponse);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(body);
    }
}
