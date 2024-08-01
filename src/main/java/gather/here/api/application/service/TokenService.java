package gather.here.api.application.service;

import gather.here.api.application.dto.response.GetTokenResponseDto;
import gather.here.api.domain.security.AccessTokenFactory;
import gather.here.api.domain.security.RefreshTokenFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;


import java.security.KeyStore;

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

    public String accessTokenGenerate(String identity){
        return accessTokenFactory.generate(identity,getKey(),ACCESS_TOKEN_MINUTE);
    }

    public Authentication accessTokenValidate(String token){
        return accessTokenFactory.validate(token,getKey());
    }
    public String refreshTokenGenerate(Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String identity = userDetails.getUsername();
        String refreshToken = refreshTokenFactory.generate(identity, getKey(), REFRESH_TOKEN_MINUTE);

        refreshTokenFactory.update(identity, refreshToken);
        return withTokenPrefix(refreshToken);
    }

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET);
        Key key = Keys.hmacShaKeyFor(keyBytes);
        return key;
    }
    private String withTokenPrefix(String token) {
        return "Bearer " + token;
    }
}
