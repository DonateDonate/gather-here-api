package gather.here.api.infra.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import gather.here.api.global.exception.CustomResponseBody;
import gather.here.api.global.exception.ResponseStatus;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        JwtException exception = (JwtException) request.getAttribute("exception");

        // 토큰 만료의 경우
        if (exception instanceof ExpiredJwtException) {
            sendErrorResponse(response, ResponseStatus.EXPIRE_TOKEN);
            return;
        }

        // 유효한 토큰이 아닌 경우
        if (exception != null) {
            sendErrorResponse(response,ResponseStatus.INVALID_TOKEN);
        }
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
