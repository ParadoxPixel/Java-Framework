package nl.iobyte.framework.generic.exceptional;

import nl.iobyte.framework.FW;
import nl.iobyte.framework.generic.invoker.enums.TaskType;

import java.util.concurrent.CompletableFuture;

public class ExceptionalFuture<T> implements Runnable {

    private final CompletableFuture<T> future;
    private final ExceptionalSupplier<T> supplier;

    public ExceptionalFuture(ExceptionalSupplier<T> supplier) {
        this.future = new CompletableFuture<>();
        this.supplier = supplier;
    }

    @Override
    public void run() {
        try {
            T result = supplier.get();
            future.complete(result);
        } catch(Exception e) {
            future.completeExceptionally(e);
        }
    }

    /**
     * Schedule future
     *
     * @return CompletableFuture<T>
     */
    public CompletableFuture<T> schedule() {
        return schedule(TaskType.INTERNAL);
    }

    /**
     * Schedule future
     *
     * @return CompletableFuture<T>
     */
    public CompletableFuture<T> schedule(TaskType type) {
        FW.getInvoker()
          .getScheduler()
          .run(this, type);

        return future;
    }

    public static <T> ExceptionalFuture<T> of(ExceptionalSupplier<T> supplier) {
        return new ExceptionalFuture<>(supplier);
    }

}
