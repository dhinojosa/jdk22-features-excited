package com.evolutionnext.streamgatherers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import static com.evolutionnext.streamgatherers.Zippers.*;
public class ZipperGatherer {


    public static void main(String[] args) {
        zipPairs();
        groceryLists();
//        attemptFibonacci();
    }

    private static void attemptFibonacci() {
        List<Integer> result = fibonacci().limit(100).toList();
        System.out.println(result);
    }

    private static void groceryLists() {
        List<String> letters = List.of("Eggs", "Milk", "Bread", "Tea", "Apricots");
        Stream<Integer> numbers = IntStream.range(1, 1000)
            .boxed();
        List<String> zipped = letters.stream()
            .gather(zip(numbers).with((letter, i) -> i + ". " + letter))
            .collect(Collectors.toList());
        System.out.println(zipped);
    }

    private static void zipPairs() {
        List<String> letters = List.of("a", "b", "c", "d", "e");
        Stream<Integer> numbers = IntStream.range(0, letters.size())
            .boxed();

        List<String> zipped = letters.stream()
            .gather(zip(numbers).with((letter, i) -> i + "-" + letter))
            .collect(Collectors.toList());

        System.out.println(zipped);
    }

    public static Stream<Integer> fibonacci() {
        return Stream.concat(Stream.of(0, 1),
            fibonacci().gather(zip(fibonacci().skip(1)).with(Integer::sum)));
    }
}
