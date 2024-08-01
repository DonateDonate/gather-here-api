package gather.here.api.domain.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    private String token;

    private String identity;

    public RefreshToken(String token, String identity) {
        this.token = token;
        this.identity = identity;
    }

    public void updateToken(String token) {
        this.token = token;
    }

    public void deleteToken() {
        this.token = null;
    }
}
