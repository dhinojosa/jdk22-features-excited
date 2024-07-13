package com.evolutionnext.vector;

import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.VectorSpecies;

import java.util.Arrays;

/**
 * Masking may not work, if it doesn't you can use loopBound to determine what can
 * fit and for the remainder use a classical loop
 */
public class AddStandardVectorLoopWithoutMask {
    static final VectorSpecies<Float> SPECIES =
        FloatVector.SPECIES_PREFERRED;

    static void vectorComputation(float[] a, float[] b, float[] c) {
        //initialize here outside the loop
        int index = 0;

        //Here we will replace the index with loopBound
        for (; index < SPECIES.loopBound(a.length); index += SPECIES.length()) {
            //The mask will perform the splices by masking the values needed for computation
            var va = FloatVector.fromArray(SPECIES, a, index);
            var vb = FloatVector.fromArray(SPECIES, b, index);
            var vc = va.add(vb);
            vc.intoArray(c, index);
        }

        //For the remaining we move outside the SIMD and run classically
        for (int index2 = index; index2 < a.length; index2 ++) {
            c[index2] = a[index2] + b[index2];
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
        System.out.printf("The size of the species is %d%n", SPECIES.length());
        System.out.printf("The amount of memory available is therefore %d%n", Float.SIZE * SPECIES.length());

        //Now we will see that it computes
        System.out.println(Arrays.toString(c));
    }
}
