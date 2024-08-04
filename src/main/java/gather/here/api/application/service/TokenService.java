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

import java.security.Key;
import java.util.Optional;

import static gather.here.api.global.util.TokenUtil.removePrefix;
import static gather.here.api.global.util.TokenUtil.withTokenPrefix;

@RequiredArgsConstructor
public class TokenService {

    @Value("${security.jwt.secret}")
    private String JWT_SECRET ;

    @Value("${security.jwt.access-token.minute}")
    private long ACCESS_TOKEN_MINUTE;

    @Value("${security.jwt.refresh-token.minute}")
    private long REFRESH_TOKEN_MINUTE;

    @Value("${security.jwt.access-token.prefix}")
    private String ACCESS_TOKEN_PREFIX;

    @Value("${security.jwt.refresh-token.prefix}")
    private String REFRESH_TOKEN_PREFIX;

    private final AccessTokenFactory accessTokenFactory;
    private final RefreshTokenFactory refreshTokenFactory;

    public String accessTokenGenerate(Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = accessTokenFactory.generate(userDetails.getUsername(), getKey(), ACCESS_TOKEN_MINUTE);
        return withTokenPrefix(accessToken,ACCESS_TOKEN_PREFIX);
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
        return withTokenPrefix(refreshToken,REFRESH_TOKEN_PREFIX);
    }

    public TokenResponseDto reissue(String refreshTokenWithPrefix){
        String refreshToken = removePrefix(refreshTokenWithPrefix,REFRESH_TOKEN_PREFIX);
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
};
