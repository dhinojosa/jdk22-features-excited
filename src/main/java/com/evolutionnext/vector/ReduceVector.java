package com.evolutionnext.vector;

import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

import java.util.Arrays;

public class ReduceVector {
    static final VectorSpecies<Float> SPECIES =
        FloatVector.SPECIES_PREFERRED;

    static double vectorComputation(float[] a) {
        float sum = 0f;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            //Assuming we can use a mask,
            //if we don't then we must
            var mask = SPECIES.indexInRange(i, a.length);
            var va = FloatVector.fromArray(SPECIES, a, i, mask);
            var vb = va.mul(va, mask);
            //Cross-lane computation, we are reducing
            sum += vb.reduceLanes(VectorOperators.ADD, mask);
        }
        return Math.sqrt(sum);
    }

    public static void main(String[] args) {
        float[] a = {0.23f, 0.45f, 0.49f, 0.90f, 0.01f, 0.88f, 0.79f, 0.45f, 0.21f, 0.93f};
        System.out.printf("a length %d%n", a.length);
        double sumOfSquares = vectorComputation(a);
        System.out.printf("Sum of Squares %f%n", sumOfSquares);
    }
}
