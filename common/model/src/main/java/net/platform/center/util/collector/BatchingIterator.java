
package net.platform.center.util.collector;

import static java.util.Spliterator.ORDERED;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @Author: bing zi
 * @Date: 2021/8/3 14:27
 */
public class BatchingIterator<T, C extends Collection<T>> implements Iterator<C> {

    private final int batchSize;

    private C currentBatch;

    private final Iterator<T> sourceIterator;

    private final Supplier<C> supplier;

    public BatchingIterator(Iterator<T> sourceIterator, int batchSize, Supplier<C> supplier) {
        this.batchSize = batchSize;
        this.sourceIterator = sourceIterator;
        this.supplier = supplier;
    }

    public static <T, C extends Collection<T>> Stream<C> batchedStreamOf(Stream<T> originalStream, int batchSize,
        Supplier<C> supplier) {
        return asStream(new BatchingIterator<>(originalStream.iterator(), batchSize, supplier));
    }

    private static <T> Stream<T> asStream(Iterator<T> iterator) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, ORDERED), false);
    }

    @Override
    public boolean hasNext() {
        prepareNextBatch();
        return currentBatch != null && !currentBatch.isEmpty();
    }

    @Override
    public C next() {
        if (currentBatch == null || currentBatch.isEmpty()) {
            throw new NoSuchElementException();
        }
        return currentBatch;
    }

    private void prepareNextBatch() {
        currentBatch = supplier.get();
        while (sourceIterator.hasNext() && currentBatch.size() < batchSize) {
            currentBatch.add(sourceIterator.next());
        }
    }
}
