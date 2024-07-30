package gather.here.api.infra.persistence;

import gather.here.api.domain.entities.Token;
import gather.here.api.domain.repositories.TokenRepository;
import gather.here.api.infra.persistence.jpa.TokenJpaRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TokenRepositoryImpl implements TokenRepository {
    private final TokenJpaRepository tokenJpaRepository;

    @Override
    public void save(Token token) {

    }
}
