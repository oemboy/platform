
package net.platform.center.util.collector;

import java.util.Collection;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @Author: bing zi
 * @Date: 2021/8/3 14:27
 */
public class BatchSpliterator<E, C extends Collection<E>> implements Spliterator<Collection<E>> {

    private final Spliterator<E> base;

    private final int batchSize;

    private final Supplier<C> supplier;

    public BatchSpliterator(Spliterator<E> base, int batchSize, Supplier<C> supplier) {
        this.base = base;
        this.batchSize = batchSize;
        this.supplier = supplier;
    }

    public static <E, C extends Collection<E>> Stream<Collection<E>> batches(Stream<E> stream, int batchSize,
        Supplier<C> supplier, Collector<E, ?, C> collector) {
        return batchSize <= 0 ? Stream.of(stream.collect(collector)) : StreamSupport
            .stream(new BatchSpliterator<>(stream.spliterator(), batchSize, supplier), stream.isParallel());
    }

    @Override
    public boolean tryAdvance(Consumer<? super Collection<E>> action) {
        final C collection = supplier.get();

        for (int i = 0; i < batchSize; i++) {
            if (!base.tryAdvance(collection::add)) {
                break;
            }
        }

        if (collection.isEmpty()) {
            return false;
        }

        action.accept(collection);
        return true;
    }

    @Override
    public Spliterator<Collection<E>> trySplit() {
        if (base.estimateSize() <= batchSize) {
            return null;
        }

        final Spliterator<E> splitBase = this.base.trySplit();
        return splitBase == null ? null : new BatchSpliterator<>(splitBase, batchSize, supplier);
    }

    @Override
    public long estimateSize() {
        final double baseSize = base.estimateSize();
        return baseSize == 0 ? 0 : (long) Math.ceil(baseSize / batchSize);
    }

    @Override
    public int characteristics() {
        return base.characteristics();
    }
}
