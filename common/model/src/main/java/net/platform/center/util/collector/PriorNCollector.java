
package net.platform.center.util.collector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;

/**
 * @Author: bing zi
 * @Date: 2021/8/3 14:27
 */
public class PriorNCollector<T> {
    final int limit;

    final Comparator<? super T> comparator;

    final Comparator<? super T> reverse;

    final BiConsumer<PriorityQueue<T>, T> accumulator;

    final BinaryOperator<PriorityQueue<T>> combiner;

    final Function<PriorityQueue<T>, List<T>> finisher;

    public PriorNCollector(int limit, Comparator<? super T> comparator) {
        this.limit = limit;
        this.comparator = comparator;
        this.reverse = comparator.reversed();

        this.accumulator = (queue, t) -> {
            queue.add(t);
            if (queue.size() > limit) {
                queue.poll();
            }
        };

        this.combiner = (q1, q2) -> {
            for (T t : q2) {
                accumulator.accept(q1, t);
            }
            return q1;
        };

        this.finisher = (PriorityQueue<T> queue) -> {
            List<T> list = new ArrayList<>(queue);
            Collections.reverse(list);
            return list;
        };
    }

    public Collector<T, ?, List<T>> maxN() {
        return Collector.of(() -> new PriorityQueue<>(limit + 1, comparator), accumulator, combiner, finisher);
    }

    public Collector<T, ?, List<T>> minN() {
        return Collector.of(() -> new PriorityQueue<>(limit + 1, reverse), accumulator, combiner, finisher);
    }
}