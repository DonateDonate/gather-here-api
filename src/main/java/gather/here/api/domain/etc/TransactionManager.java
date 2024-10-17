package gather.here.api.domain.etc;

public interface TransactionManager {
    <K, V> void transaction(TransactionCommand<K, V> command);

    @FunctionalInterface
    interface TransactionCommand<K, V> {
        void execute();
    }
}
