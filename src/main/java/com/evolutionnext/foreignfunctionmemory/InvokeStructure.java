package com.evolutionnext.foreignfunctionmemory;

import java.lang.foreign.*;
import java.lang.invoke.VarHandle;

public class InvokeStructure {

    public static void main(String[] args) {

        //This is the more manual way to invoke that structure
        try (Arena arena = Arena.ofConfined()) {

            //Attempting to create a struct downcall
            // struct Point {
            //   int x;
            //   int y;
            //} pts[10];

            int numElementsInStruct = 2;
            int numberOfBytes = 4;
            int numberOfStructsInArray = 10;

            MemorySegment segment =
                arena.allocate((long)(numElementsInStruct * numberOfBytes * numberOfStructsInArray), 1);

            //Add the elements of the struct
            for (int i = 0; i < 10; i++) {
                int xValue = i;
                int yValue = i * 10;
                segment.setAtIndex(ValueLayout.JAVA_INT, (i * 2),     xValue);
                segment.setAtIndex(ValueLayout.JAVA_INT, (i * 2) + 1, yValue);
            }

            //Read the elements back
            for (int i = 0; i < 10; i++) {
                int xVal = segment.getAtIndex(ValueLayout.JAVA_INT, (i * 2));
                int yVal = segment.getAtIndex(ValueLayout.JAVA_INT, (i * 2) + 1);
                System.out.println("(" + xVal + ", " + yVal + ")");
            }
        }

        //Starting another confined arena,
        //To avoid these calculations like the above, you can use a memory layout.
        try (Arena arena = Arena.ofConfined()) {

            //Create the struct
            // struct Point {
            //   int x;
            //   int y;
            //} pts[10];

            StructLayout structLayout = MemoryLayout.structLayout(
                ValueLayout.JAVA_INT.withName("x"),
                ValueLayout.JAVA_INT.withName("y"));

            //SequenceLayout is the array
            SequenceLayout ptsLayout
                = MemoryLayout.sequenceLayout(10, structLayout);

            //Since this is an array,
            // the handle here represents the x in the struct, for every
            // element in the array
            VarHandle xHandle
                = ptsLayout.varHandle(MemoryLayout.PathElement.sequenceElement(),
                MemoryLayout.PathElement.groupElement("x"));

            //Since this is an array,
            // the handle here represents the y in the struct, for every
            // element in the array
            //A VarHandle is a dynamically strongly typed reference to a variable
            // or to a parametrically-defined family of variables,
            // including static fields, non-static fields, array elements,
            // or components of an off-heap data structure.
            VarHandle yHandle
                = ptsLayout.varHandle(MemoryLayout.PathElement.sequenceElement(),
                MemoryLayout.PathElement.groupElement("y"));


            //ptsLayout are the segments or the array of 10 elements
            MemorySegment segment = arena.allocate(ptsLayout);

            //Setting the Structs, by iterating every element
            for (int i = 0; i < ptsLayout.elementCount(); i++) {
                int xValue = i;
                int yValue = i * 10;

                //Set takes the following
                //`segment`: the memory segment in which to set the value
                //`0L`: the base offset, which is a long coordinate that points to the start of the array
                //`(long) i`: a second `long` coordinate that indicates the array index in which to set the value
                //`xValue` and `yValue`: the actual value to set
                xHandle.set(segment, 0L, (long) i, xValue);
                yHandle.set(segment, 0L, (long) i, yValue);
            }

            //Retrieving the Structs, by iterating again every element
            for (int i = 0; i < ptsLayout.elementCount(); i++) {
                int xVal = (int) xHandle.get(segment, 0L, (long) i);
                int yVal = (int) yHandle.get(segment, 0L, (long) i);
                System.out.println("(" + xVal + ", " + yVal + ")");
            }
        }
    }
}
