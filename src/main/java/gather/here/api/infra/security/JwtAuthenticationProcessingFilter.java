package gather.here.api.infra.security;

import gather.here.api.application.service.TokenService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtAuthenticationProcessingFilter");
        String accessToken = extractAccessTokenWithPrefix(request);

        if(!StringUtils.hasText(accessToken)){
            filterChain.doFilter(request, response);
            return;
        }
        try {
            Authentication authentication = tokenService.accessTokenValidate(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch (JwtException e){
            request.setAttribute("exception", e);
        }

        filterChain.doFilter(request, response);
    }

    private String extractAccessTokenWithPrefix(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }
}
