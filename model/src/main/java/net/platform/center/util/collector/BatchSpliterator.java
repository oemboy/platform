
package net.platform.center.util.collector;

import java.util.Collection;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author bing zi
 * @since 2021/8/3 14:27
 */
public final class BatchSpliterator<E, C extends Collection<E>> implements Spliterator<C> {

    private final Spliterator<E> base;

    private final int batchSize;

    private final boolean isSimpleCollection;

    private final Supplier<?> supplier;

    private final BiConsumer<Object, E> accumulator;

    private final Function<Object, C> finisher;

    public BatchSpliterator(Spliterator<E> base, int batchSize, Supplier<C> supplier) {
        this(base, batchSize, Collectors.toCollection(supplier), true);
    }

    public BatchSpliterator(Spliterator<E> base, int batchSize, Collector<E, ?, C> collector) {
        this(base, batchSize, collector, false);
    }

    @SuppressWarnings("unchecked")
    private BatchSpliterator(Spliterator<E> base, int batchSize, Collector<E, ?, C> collector, boolean isSimple) {
        this.base = Objects.requireNonNull(base);
        this.batchSize = batchSize;
        this.isSimpleCollection = isSimple;

        this.supplier = collector.supplier();
        this.accumulator = (BiConsumer<Object, E>) collector.accumulator();
        this.finisher = (Function<Object, C>) collector.finisher();
    }

    /**
     * 专为 trySplit 设计的克隆构造函数：直接引用拷贝，零开销。
     */
    private BatchSpliterator(Spliterator<E> splitBase, BatchSpliterator<E, C> parent) {
        this.base = splitBase;
        this.batchSize = parent.batchSize;
        this.isSimpleCollection = parent.isSimpleCollection;
        this.supplier = parent.supplier;
        this.accumulator = parent.accumulator;
        this.finisher = parent.finisher;
    }

    @Override
    public boolean tryAdvance(Consumer<? super C> action) {
        final Object container = this.supplier.get();
        int count = 0;
        for (int i = 0; i < this.batchSize; i++) {
            if (!this.base.tryAdvance(e -> this.accumulator.accept(container, e))) {
                break;
            }
            count++;
        }
        if (count == 0) {
            return false;
        }
        action.accept(this.finisher.apply(container));
        return true;
    }

    @Override
    public Spliterator<C> trySplit() {
        if (this.base.estimateSize() <= this.batchSize) {
            return null;
        }
        final Spliterator<E> splitBase = this.base.trySplit();
        return (splitBase == null) ? null : new BatchSpliterator<>(splitBase, this);
    }

    @Override
    public long estimateSize() {
        final long baseSize = this.base.estimateSize();
        if (baseSize <= 0 || baseSize == Long.MAX_VALUE) {
            return baseSize;
        }
        // 整数向上取整逻辑
        final long estimated = (baseSize + this.batchSize - 1) / this.batchSize;
        return estimated > 0 ? estimated : 1;
    }

    @Override
    public int characteristics() {
        int characteristics = base.characteristics();
        // 无论什么模式，SIZED/SUBSIZED 必须去掉，因为分批逻辑本身打破了精确计数契约
        characteristics &= ~(Spliterator.SIZED | Spliterator.SUBSIZED);
        // 核心逻辑：如果不是简单模式，必须强制去掉 DISTINCT
        if (!isSimpleCollection) {
            characteristics &= ~Spliterator.DISTINCT;
        }
        return characteristics;
    }

    public static <E, C extends Collection<E>> Stream<C> batches(Stream<E> stream, int batchSize,
        Supplier<C> supplier) {
        Objects.requireNonNull(stream);
        if (batchSize <= 0) {
            return Stream.of(stream.collect(Collectors.toCollection(supplier)));
        }
        return StreamSupport.stream(new BatchSpliterator<>(stream.spliterator(), batchSize, supplier),
            stream.isParallel());
    }

    public static <E, C extends Collection<E>> Stream<C> batches(Stream<E> stream, int batchSize,
        Collector<E, ?, C> collector) {
        Objects.requireNonNull(stream);
        if (batchSize <= 0) {
            return Stream.of(stream.collect(collector));
        }
        return StreamSupport.stream(new BatchSpliterator<>(stream.spliterator(), batchSize, collector),
            stream.isParallel());
    }

}