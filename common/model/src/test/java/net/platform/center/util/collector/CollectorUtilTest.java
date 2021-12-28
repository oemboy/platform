
package net.platform.center.util.collector;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @Author: bing zi
 * @Date: 2021/8/3 14:27
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
    void batches() {
        Stream<Collection<String>> batches =
            BatchSpliterator.batches(buildList().stream(), 2, ArrayList::new, toList());
        Assertions.assertEquals(2L, batches
            // .peek(System.out::println)
            .count());
    }

    @Test
    void priorNCollector() {
        PriorNCollector<String> priorNCollector = new PriorNCollector<>(2, Comparator.comparing(Function.identity()));

        Assertions.assertEquals("dc", String.join("", buildList().stream().collect(priorNCollector.maxN())));

        Assertions.assertEquals("ab", String.join("", buildList().stream().collect(priorNCollector.minN())));
    }

    private List<String> buildList() {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        return list;
    }

}