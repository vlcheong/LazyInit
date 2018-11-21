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
        T target = ref.get();
        if (null == target) {
            target = createAndGet();
        }
        return target;
    }

    private T createAndGet() {
        long stamp = sl.writeLock();
        try {
            T target = ref.get();
            if (target != null) {
                return target;
            }
            return ref.updateAndGet(prev -> (prev == null) ? supplier.get() : prev);
        } finally {
            sl.unlockWrite(stamp);
        }
    }
}