package gather.here.api.domain.etc;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;

public class RedisTransaction {
    
    public static <K, V> void transaction(RedisOperations<K, V> operations, TransactionCommand<K, V> command) {
        operations.execute(new SessionCallback<Void>() {
            @Override
            public <K1, V1> Void execute(RedisOperations<K1, V1> callbackOperations) throws DataAccessException {
                System.out.println("excute");
                callbackOperations.multi();
                command.execute(operations);
                callbackOperations.exec();
                return null;
            }
        });
    }

    @FunctionalInterface
    public interface TransactionCommand<K, V> {
        void execute(RedisOperations<K, V> operations);
    }
}
