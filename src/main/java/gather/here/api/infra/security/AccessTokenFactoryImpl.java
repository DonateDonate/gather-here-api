package gather.here.api.infra.security;

import gather.here.api.application.dto.response.GetTokenResponseDto;
import gather.here.api.domain.security.AccessTokenFactory;
import gather.here.api.global.exception.AuthException;
import gather.here.api.global.exception.ResponseStatus;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;

public class AccessTokenFactoryImpl implements AccessTokenFactory {

    @Override
    public String generate(String identity, Key key , long minute) {

        Claims accessTokenClaim = Jwts.claims();
        accessTokenClaim.put("identity", identity);

        ZonedDateTime now = ZonedDateTime.now();

        return Jwts.builder()
                .setClaims(accessTokenClaim)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(now.plusMinutes(minute).toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public Authentication validate(String accessTokenTokenWithPrefix, Key key) {
        String token = removePrefix(accessTokenTokenWithPrefix);
        Claims parseClaims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        String identity = parseClaims.get("identity", String.class);
        return UsernamePasswordAuthenticationToken.authenticated(identity, null,null);
    }


    private String removePrefix(String token) {
        String tokenPrefix = "Bearer";
        if (!token.startsWith(tokenPrefix + " ")) {
            throw new AuthException(ResponseStatus.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
        }
        return token.substring(tokenPrefix.length() + 1);
    }
}
