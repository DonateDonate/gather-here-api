package gather.here.api.infra.security;

import gather.here.api.domain.entities.RefreshToken;
import gather.here.api.domain.repositories.RefreshTokenRepository;
import gather.here.api.domain.security.RefreshTokenFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static gather.here.api.global.util.TokenUtil.removePrefix;

@RequiredArgsConstructor
public class RefreshTokenFactoryImpl implements RefreshTokenFactory {

    @Value("${security.jwt.refresh-token.prefix}")
    private String REFRESH_TOKEN_PREFIX;

    private final RefreshTokenRepository repository;

    @Override
    public String generate(String identity, Key key, long minute) {
        Claims refreshTokenClaim = Jwts.claims();
        refreshTokenClaim.put("identity",identity);

        ZonedDateTime now = ZonedDateTime.now();
        Date expireDate = Date.from(now.plusMinutes(minute).toInstant());
        return Jwts.builder()
                .setClaims(refreshTokenClaim)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(expireDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    @Transactional
    public void update(String identity, String token) {
        List<RefreshToken> refreshTokens = repository.findByIdentity(identity);

        if(refreshTokens.isEmpty()){
            RefreshToken refreshToken = new RefreshToken(token, identity);
            repository.save(refreshToken);
            return;
        }

        RefreshToken refreshToken = refreshTokens.get(0);
        refreshToken.updateToken(token);
    }

    @Override
    public Optional<String> find(String identity) {
        List<RefreshToken> refreshTokens = repository.findByIdentity(identity);

        if (refreshTokens.isEmpty()) {
            return Optional.empty();
        }

        RefreshToken refreshToken = refreshTokens.get(0);

        return Optional.of(refreshToken.getToken());
    }

    @Override
    @Transactional
    public void delete(String identity) {
        List<RefreshToken> refreshTokens = repository.findByIdentity(identity);

        if (refreshTokens.isEmpty()) {
            return;
        }

        RefreshToken refreshToken = refreshTokens.get(0);

        refreshToken.deleteToken();
    }

    @Override
    public Authentication validate(String refreshTokenTokenWithPrefix, Key key) {
        String token = removePrefix(refreshTokenTokenWithPrefix,REFRESH_TOKEN_PREFIX);
        Claims parseClaims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        String identity = parseClaims.get("identity", String.class);
        return UsernamePasswordAuthenticationToken.authenticated(identity, null,null);
    }
}
