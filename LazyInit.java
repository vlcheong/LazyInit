import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Supplier;

public final class LazyInit<T> {

    private final AtomicReference<T> ref;

    private final Supplier<T> supplier;

    private final StampedLock sl;

    private LazyInit(Supplier<T> supplier) {
        this.ref = new AtomicReference<>();
        this.supplier = supplier;
        this.sl = new StampedLock();
    }

    public static <T> LazyInit<T> of(Supplier<T> supplier) {
        return new LazyInit<>(supplier);
    }

    public T getOrCreate() {
        T instance = ref.get();
        if (null == instance) {
            instance = createAndGet();
        }
        return instance;
    }

    private T createAndGet() {
        long stamp = sl.writeLock();
        try {
            T instance = ref.get();
            if (instance != null) {
                return instance;
            }
            return ref.updateAndGet(prev -> (prev == null) ? supplier.get() : prev);
        } finally {
            sl.unlockWrite(stamp);
        }
    }
}