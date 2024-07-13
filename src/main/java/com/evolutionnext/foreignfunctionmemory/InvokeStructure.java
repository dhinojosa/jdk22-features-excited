package com.evolutionnext.foreignfunctionmemory;

import java.lang.foreign.*;
import java.lang.invoke.VarHandle;

public class InvokeStructure {

    public static void main(String[] args) {
        try (Arena arena = Arena.ofConfined()) {

            MemorySegment segment =
                arena.allocate((long)(2 * 4 * 10), 1);

            for (int i = 0; i < 10; i++) {
                int xValue = i;
                int yValue = i * 10;
                segment.setAtIndex(ValueLayout.JAVA_INT, (i * 2),     xValue);
                segment.setAtIndex(ValueLayout.JAVA_INT, (i * 2) + 1, yValue);
            }

            for (int i = 0; i < 10; i++) {
                int xVal = segment.getAtIndex(ValueLayout.JAVA_INT, (i * 2));
                int yVal = segment.getAtIndex(ValueLayout.JAVA_INT, (i * 2) + 1);
                System.out.println("(" + xVal + ", " + yVal + ")");
            }
        }

        try (Arena arena = Arena.ofConfined()) {

            StructLayout structLayout = MemoryLayout.structLayout(
                ValueLayout.JAVA_INT.withName("x"),
                ValueLayout.JAVA_INT.withName("y"));

            SequenceLayout ptsLayout
                = MemoryLayout.sequenceLayout(10, structLayout);

            VarHandle xHandle
                = ptsLayout.varHandle(MemoryLayout.PathElement.sequenceElement(),
                MemoryLayout.PathElement.groupElement("x"));

            VarHandle yHandle
                = ptsLayout.varHandle(MemoryLayout.PathElement.sequenceElement(),
                MemoryLayout.PathElement.groupElement("y"));

            MemorySegment segment = arena.allocate(ptsLayout);

            //Setting the Structs
            for (int i = 0; i < ptsLayout.elementCount(); i++) {
                int xValue = i;
                int yValue = i * 10;
                xHandle.set(segment, 0L, (long) i, xValue);
                yHandle.set(segment, 0L, (long) i, yValue);
            }

            //Retrieving the Structs
            for (int i = 0; i < ptsLayout.elementCount(); i++) {
                int xVal = (int) xHandle.get(segment, 0L, (long) i);
                int yVal = (int) yHandle.get(segment, 0L, (long) i);
                System.out.println("(" + xVal + ", " + yVal + ")");
            }
        }
    }
}
