package gather.here.api.domain.security;

import org.springframework.security.core.Authentication;

import java.security.Key;
import java.util.Optional;

public interface RefreshTokenFactory {
    String generate(String identity, Key key, long minute);
    void update(String identity, String token);

    Optional<String> find(String identity);
    void delete(String identity);
    Authentication validate(String refreshTokenTokenWithPrefix, Key key);
}
