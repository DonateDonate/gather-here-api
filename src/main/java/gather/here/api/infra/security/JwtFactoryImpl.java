package gather.here.api.infra.security;

import gather.here.api.application.dto.response.GetTokenResponseDto;
import gather.here.api.domain.security.JwtFactory;
import gather.here.api.domain.security.SecurityMemberInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;

public class JwtFactoryImpl implements JwtFactory {

    @Value("${security.jwt.secret}")
    private String JWT_SECRET ;

    @Value("${security.jwt.access-token.minute}")
    private long ACCESS_TOKEN_MINUTE;

    @Value("${security.jwt.refresh-token.minute}")
    private long REFRESH_TOKEN_MINUTE;

    @Override
    public GetTokenResponseDto generate(String identity, long memberSeq) {

        Claims accessTokenClaim = Jwts.claims();
        accessTokenClaim.put("identity",identity);
        accessTokenClaim.put("memberSeq", memberSeq);

        Key key = getKey();

        ZonedDateTime now = ZonedDateTime.now();

        String accessToken = Jwts.builder()
                .setClaims(accessTokenClaim)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(now.plusMinutes(ACCESS_TOKEN_MINUTE).toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();


        Claims refreshTokenClaim = Jwts.claims();
        refreshTokenClaim.put("memberSeq", memberSeq);

        String refreshToken = Jwts.builder()
                .setClaims(refreshTokenClaim)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(now.plusMinutes(REFRESH_TOKEN_MINUTE).toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return new GetTokenResponseDto("Bearer", accessToken,refreshToken);
    }

    @Override
    public boolean validate(String token) {
        Key key = getKey();
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public String getIdentity(String token) {
        Key key = getKey();
        Claims parseClaims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return parseClaims.get("identity", String.class);
    }

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET);
        Key key = Keys.hmacShaKeyFor(keyBytes);
        return key;
    }
}
