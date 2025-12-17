
package net.platform.center.util.collector;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author bing zi
 * @since 2021/8/3 14:27
 */
class CollectorUtilTest {

    @Test
    void batchedStreamOf() {
        Stream<ArrayList<String>> listStream =
            BatchingIterator.batchedStreamOf(buildList().stream(), 2, ArrayList::new);
        Assertions.assertEquals(2L, listStream
            // .peek(System.out::println)
            .count());
    }

    @Test
    void batchesSupplier() {
        Stream<Collection<String>> batches = BatchSpliterator.batches(buildList().stream(), 2, ArrayList::new);
        Assertions.assertEquals(2L, batches
            // .peek(System.out::println)
            .count());
    }

    @Test
    void batchesCollector() {
        Stream<List<String>> batches = BatchSpliterator.batches(buildList().stream(), 2, toList());
        Assertions.assertEquals(2L, batches
                // .peek(System.out::println)
                .count());
    }

    @Test
    void priorNCollector() {
        PriorCollector<String> priorCollector = new PriorCollector<>(2, Comparator.comparing(Function.identity()));

        Assertions.assertEquals("dc", String.join("", buildList().stream().collect(priorCollector.maxN())));

        Assertions.assertEquals("ab", String.join("", buildList().stream().collect(priorCollector.minN())));
    }

    private List<String> buildList() {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        return list;
    }

    @Test
    void countEggs() {
        List<Integer> collect = IntStream.iterate(0, i -> i + 1)
            .filter(num -> num % 3 == 1 && num % 5 == 2 && num % 7 == 6)
            .limit(10)
            .peek(System.out::println)
            .boxed()
            .toList();
        assertEquals(10, collect.size());
    }
}