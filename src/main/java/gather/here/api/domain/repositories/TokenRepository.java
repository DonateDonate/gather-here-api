package gather.here.api.domain.repositories;

import gather.here.api.domain.entities.Token;

public interface TokenRepository {
    void save(Token token);

}
