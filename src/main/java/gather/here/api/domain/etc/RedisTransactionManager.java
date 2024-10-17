package gather.here.api.domain.etc;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;

@RequiredArgsConstructor
public class RedisTransactionManager implements TransactionManager{
    private final RedisOperations<String,Object> redisOperations;

    @Override
    public <K, V> void transaction(TransactionCommand<K, V> command) {
        redisOperations.execute(new SessionCallback<Void>() {
            @Override
            public <K1, V1> Void execute(RedisOperations<K1, V1> operations) throws DataAccessException {
                operations.multi();
                command.execute();
                operations.exec();
                return null;
            }
        });
    }
}
