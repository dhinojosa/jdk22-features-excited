package com.evolutionnext.vector;

import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

import java.util.Arrays;

public class CompareVector {
    static final VectorSpecies<Float> SPECIES =
        FloatVector.SPECIES_PREFERRED;

    static void vectorComputation(float[] a) {
        float[] result = new float[a.length];
        int maxIndex = 0;
        System.out.printf("The species length (number of lanes) is %d%n", SPECIES.length());
        //We are splitting the bounds because we can only have a certain number of lanes
        //available for us to use
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            var mask = SPECIES.indexInRange(i, a.length);
            var va = FloatVector.fromArray(SPECIES, a, i, mask);
            var compare = va.compare(VectorOperators.GT, .50f, mask);
            //cross lane operation removes the components where the mask is false
            va.compress(compare).intoArray(result, maxIndex);
            maxIndex += compare.trueCount();
        }

        System.out.printf("i is already at %d%n", maxIndex);
        System.out.printf("a.length is at %d%n", a.length);

        //truncating the array to max index
        result = Arrays.copyOf(result, maxIndex);
        System.out.printf("result: %s%n", Arrays.toString(result));

    }

    public static void main(String[] args) {

        float[] a = {0.23f, 0.45f, 0.49f, 0.90f, 0.01f, 0.88f, 0.79f, 0.45f, 0.21f, 0.93f};
        System.out.printf("a length %d%n", a.length);

        vectorComputation(a);
    }
}
