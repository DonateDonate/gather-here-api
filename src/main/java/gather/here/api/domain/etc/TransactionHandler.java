package gather.here.api.domain.etc;

public interface TransactionHandler {
    <K, V> void transaction(TransactionCommand<K, V> command);

    @FunctionalInterface
    interface TransactionCommand<K, V> {
        void execute();
    }
}
