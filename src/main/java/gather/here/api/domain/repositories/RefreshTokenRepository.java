package gather.here.api.domain.repositories;

import gather.here.api.domain.entities.RefreshToken;

import java.util.List;

public interface RefreshTokenRepository {
    List<RefreshToken> findByIdentity(String identity);
    void save(RefreshToken refreshToken);
}
