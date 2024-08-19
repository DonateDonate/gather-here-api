package gather.here.api.domain.security;

import org.springframework.security.core.Authentication;

import java.security.Key;

public interface AccessTokenFactory {
    String generate(String identity, Long memberSeq,Key key, long minute);
    Authentication validate(String token, Key key);
}
