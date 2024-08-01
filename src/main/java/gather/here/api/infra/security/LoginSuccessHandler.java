package gather.here.api.infra.security;

import gather.here.api.application.service.TokenService;
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
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //accessToken 생성
        String token = tokenService.accessTokenGenerate(authentication);

        //refresh token 생성 및 저장
        String refreshToken = tokenService.refreshTokenGenerate(authentication);

        response.setHeader("Authorization","Bearer"+ " "+token);
        response.setHeader("Refresh-token","Bearer"+ " "+refreshToken);
        response.setStatus(HttpStatus.OK.value());
    }
}
