import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public final class LazyInit<T> {

    private final AtomicReference<T> ref;

    private final Supplier<T> provider;

    private final Lock writeLock;

    private LazyInit(Supplier<T> provider) {
        this.ref = new AtomicReference<>();
        this.provider = provider;
        this.writeLock = new ReentrantReadWriteLock().writeLock();
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
        writeLock.lock();
        try {
            T instance = ref.get();
            if (instance != null) {
                return instance;
            }
            return ref.updateAndGet(prev -> (prev == null) ? provider.get() : prev);
        } finally {
            writeLock.unlock();
        }
    }
}