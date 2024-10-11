package com.evolutionnext.streamgatherers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Gatherer;
import java.util.stream.IntStream;

public class SimpleGathererWithStatic {

    private static boolean isPrime(int i) {
        if (i <= 1) return false;
        if (i == 2) return true;
        if (i % 2 == 0) return false;
        for (int j = 3; j * j <= i; j += 2) {
            if (i % j == 0) return false;
        }
        return true;
    }

    private static Gatherer.Integrator<ArrayList<Integer>, Integer, List<Integer>> integrator = (state, element, downstream) -> {
        state.add(element);
        if (isPrime(state.stream().mapToInt(x -> x).sum()) || state.size() == 10) {
            ArrayList<Integer> downstreamList = new ArrayList<>(state);
            downstream.push(downstreamList);
            state.removeAll(downstreamList);
        }
        return true;
    };

    public static void main(String[] args) {
        Gatherer<Integer, ArrayList<Integer>, List<Integer>> gatherer =
            Gatherer.of(ArrayList::new, integrator, (integers, integers2) -> {
                integers.addAll(integers2);
                return integers;
            }, (integers, downstream) -> downstream.push(integers));

        System.out.println(IntStream.rangeClosed(1, 100).boxed().gather(gatherer).collect(Collectors.toList()));
    }
}
