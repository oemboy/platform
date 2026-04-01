
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
 * @author bing zi
 * @since 2021/8/3 14:27
 */
public class PriorCollector<T> {
    final int limit;

    final Comparator<? super T> comparator;

    final Comparator<? super T> reverse;

    final BiConsumer<PriorityQueue<T>, T> accumulator;

    final BinaryOperator<PriorityQueue<T>> combiner;

    final Function<PriorityQueue<T>, List<T>> finisher;

    public PriorCollector(int limit, Comparator<? super T> comparator) {
        this.limit = limit;
        this.comparator = comparator;
        this.reverse = comparator.reversed();

        this.accumulator = (queue, t) -> {
            if (queue.size() < limit) {
                queue.add(t);
            } else {
                // 核心：直接使用当前堆自己的比较器来判断！
                // 无论它是升序还是降序，peek() 永远是那个“在当前规则下最该被淘汰”的元素
                Comparator<? super T> qComp = queue.comparator();
                if (qComp.compare(t, queue.peek()) > 0) {
                    queue.poll();
                    queue.add(t);
                }
            }
        };

        this.combiner = (q1, q2) -> {
            for (T t : q2) {
                accumulator.accept(q1, t);
            }
            return q1;
        };

        this.finisher = (PriorityQueue<T> queue) -> {
            int size = queue.size();
            List<T> list = new ArrayList<>(size);
            while (!queue.isEmpty()) {
                list.add(queue.poll());
            }
            // 关键点：这里统一翻转，确保 maxN 返回降序，minN 返回升序
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