package gather.here.api.infra.persistence;

import gather.here.api.domain.entities.RefreshToken;
import gather.here.api.domain.repositories.RefreshTokenRepository;
import gather.here.api.infra.persistence.jpa.RefreshTokenJpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository repository;
    @Override
    public List<RefreshToken> findByIdentity(String identity) {
        return repository.findByIdentity(identity);
    }

    @Override
    public void save(RefreshToken refreshToken) {
        repository.save(refreshToken);
    }
}
