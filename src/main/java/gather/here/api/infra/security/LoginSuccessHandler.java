package gather.here.api.infra.security;

import gather.here.api.domain.service.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenService tokenService;

    private final String ACCESS_TOKEN_HEADER_NAME;

    private final String REFRESH_TOKEN_HEADER_NAME;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //accessToken 생성
        String accessTokenWithPrefix = tokenService.accessTokenGenerate(authentication);

        //refresh token 생성 및 저장
        String refreshTokenWithPrefix = tokenService.refreshTokenGenerate(authentication);

        response.setHeader(ACCESS_TOKEN_HEADER_NAME,accessTokenWithPrefix);
        response.setHeader(REFRESH_TOKEN_HEADER_NAME,refreshTokenWithPrefix);
        response.setStatus(HttpStatus.OK.value());
    }
}
