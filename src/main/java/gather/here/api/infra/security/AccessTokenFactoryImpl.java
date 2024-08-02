package gather.here.api.infra.security;

import gather.here.api.domain.security.AccessTokenFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;

import static gather.here.api.global.util.TokenUtil.removePrefix;

public class AccessTokenFactoryImpl implements AccessTokenFactory {

    @Value("${security.jwt.access-token.prefix}")
    private String ACCESS_TOKEN_PREFIX;

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
        String token = removePrefix(accessTokenTokenWithPrefix,ACCESS_TOKEN_PREFIX);
        Claims parseClaims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        String identity = parseClaims.get("identity", String.class);
        return UsernamePasswordAuthenticationToken.authenticated(identity, null,null);
    }
}
