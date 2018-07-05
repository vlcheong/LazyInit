import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public final class LazyInit<T> {

    private final AtomicReference<T> ref;

    private final Supplier<T> supplier;

    private final ReentrantReadWriteLock.WriteLock writeLock;

    private LazyInit(Supplier<T> supplier) {
        this.ref = new AtomicReference<>();
        this.supplier = supplier;
        this.writeLock = new ReentrantReadWriteLock().writeLock();
    }

    public static <T> LazyInit<T> of(Supplier<T> supplier) {
        return new LazyInit<>(supplier);
    }

    public T getOrCreate() {
        T target = ref.get();
        if (null == target) {
            target = createAndGet();
        }
        return target;
    }

    private T createAndGet() {
        writeLock.lock();
        try {
            T target = ref.get();
            if (target != null) {
                return target;
            }
            return ref.updateAndGet(prev -> (prev == null) ? supplier.get() : prev);
        } finally {
            writeLock.unlock();
        }
    }
}