package gather.here.api.infra.security;

import gather.here.api.domain.service.TokenService;
import gather.here.api.domain.service.dto.response.TokenResponseDto;
import gather.here.api.global.exception.AuthException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {
    @Value("${security.jwt.access-token.prefix}")
    private String ACCESS_TOKEN_PREFIX;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = extractAccessTokenWithPrefix(request);
        String refreshToken = extractRefreshTokenWithPrefix(request);

        // refresh token 포함 == 재발급의 경우
        if (StringUtils.hasText(refreshToken)) {
            try {
                TokenResponseDto tokenDto = tokenService.reissue(refreshToken);
                accessToken = tokenDto.getAccessToken();
                sendReissueSuccessResponse(response, tokenDto);

            } catch (JwtException e) {
                request.setAttribute("exception", e);
                request.setAttribute("type", "refresh");
                filterChain.doFilter(request, response);
                return;
            } catch (AuthException e){
                request.setAttribute("exception", e);
                filterChain.doFilter(request, response);
                return;
            }
        }

        if(!StringUtils.hasText(accessToken)){
            filterChain.doFilter(request, response);
            return;
        }
        try {
            Authentication authentication = tokenService.accessTokenValidate(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch (JwtException e){
            request.setAttribute("type", "access");
            request.setAttribute("exception", e);
        }

        filterChain.doFilter(request, response);
    }

    private void sendReissueSuccessResponse(HttpServletResponse response, TokenResponseDto tokenResponseDto) {
        response.setHeader("Authorization", tokenResponseDto.getAccessToken());
        response.setHeader("Refresh-token", tokenResponseDto.getRefreshToken());
    }

    private String extractAccessTokenWithPrefix(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    private String extractRefreshTokenWithPrefix(HttpServletRequest request) {
        return request.getHeader("Refresh-token");
    }
}
