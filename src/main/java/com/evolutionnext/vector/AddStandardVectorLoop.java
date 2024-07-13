package com.evolutionnext.vector;

import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.VectorSpecies;

import java.util.Arrays;

public class AddStandardVectorLoop {
    static final VectorSpecies<Float> SPECIES =
        FloatVector.SPECIES_PREFERRED;

    static void vectorComputation(float[] a, float[] b, float[] c) {
        for (int index = 0; index < a.length; index += SPECIES.length()) {
            //An error may happen around here, since the size
            //of elements doesn't match exactly
            var va = FloatVector.fromArray(SPECIES, a, index);
            var vb = FloatVector.fromArray(SPECIES, b, index);
            var vc = va.add(vb);
            vc.intoArray(c, index);
        }
    }

    public static void main(String[] args) {
        float[] a = {34.2f, 89.8f, 45.9f, 78.3f, 67.5f, 12.6f, 23.9f, 101.8f, 90.7f, 85.6f};
        float[] b = {24.5f, 32.6f, 38.9f, 28.2f, 14.3f, 67.5f, 54.5f, 77.8f, 46.5f, 89.5f};
        float[] c = new float[a.length];

        System.out.printf("a length %d%n", a.length);
        System.out.printf("b length %d%n", b.length);
        System.out.printf("c length %d%n", c.length);

        vectorComputation(a, b, c);
        //Why does it look the way it does?
        //A: Overall size of the vector is limited
        System.out.printf("The size of the species is %d%n", SPECIES.length());
        System.out.printf("The amount of memory available is therefore %d%n", Float.SIZE * SPECIES.length());
        System.out.println(Arrays.toString(c));
    }
}
