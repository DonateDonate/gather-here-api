package gather.here.api.application.service;

import gather.here.api.application.dto.response.TokenResponseDto;
import gather.here.api.domain.security.AccessTokenFactory;
import gather.here.api.domain.security.RefreshTokenFactory;
import gather.here.api.global.exception.AuthException;
import gather.here.api.global.exception.ResponseStatus;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.session.InMemoryWebSessionStore;

import java.security.Key;
import java.util.Optional;

@RequiredArgsConstructor
public class TokenService {

    @Value("${security.jwt.secret}")
    private String JWT_SECRET ;

    @Value("${security.jwt.access-token.minute}")
    private long ACCESS_TOKEN_MINUTE;

    @Value("${security.jwt.refresh-token.minute}")
    private long REFRESH_TOKEN_MINUTE;

    private final AccessTokenFactory accessTokenFactory;
    private final RefreshTokenFactory refreshTokenFactory;

    public String accessTokenGenerate(Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return accessTokenFactory.generate(userDetails.getUsername(),getKey(),ACCESS_TOKEN_MINUTE);
    }

    public Authentication accessTokenValidate(String token){
        return accessTokenFactory.validate(token,getKey());
    }
    @Transactional
    public String refreshTokenGenerate(Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String identity = userDetails.getUsername();
        String refreshToken = refreshTokenFactory.generate(identity, getKey(), REFRESH_TOKEN_MINUTE);

        refreshTokenFactory.update(identity, refreshToken);
        return withTokenPrefix(refreshToken);
    }

    public TokenResponseDto reissue(String refreshTokenWithPrefix){
        String refreshToken = removePrefix(refreshTokenWithPrefix);
        Authentication authentication = refreshTokenFactory.validate(refreshTokenWithPrefix, getKey());
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String identity = userDetails.getUsername();

        Optional<String> savedRefresh = refreshTokenFactory.find(identity);

        if(savedRefresh.isEmpty() || !refreshToken.equals(savedRefresh.get())){
            refreshTokenFactory.delete(identity);
            throw new AuthException(ResponseStatus.INVALID_TOKEN,HttpStatus.UNAUTHORIZED);
        }

        String newAccessToken = accessTokenFactory.generate(identity, getKey(), ACCESS_TOKEN_MINUTE);
        String newRefreshToken = refreshTokenFactory.generate(identity, getKey(), REFRESH_TOKEN_MINUTE);

        return new TokenResponseDto(newAccessToken, newRefreshToken);
    }


    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET);
        Key key = Keys.hmacShaKeyFor(keyBytes);
        return key;
    }
    private String withTokenPrefix(String token) {
        return "Bearer " + token;
    }
    private String removePrefix(String token) {
        String tokenPrefix = "Bearer";
        if (!token.startsWith(tokenPrefix + " ")) {
            throw new AuthException(ResponseStatus.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
        }
        return token.substring(tokenPrefix.length() + 1);
    }
}
