package com.evolutionnext.foreignfunctionmemory;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.util.function.Consumer;

public class InvokeReturningPointers {
    static MemorySegment allocateMemory(long byteSize, Arena arena) throws Throwable {

        // Obtain an instance of the native linker
        Linker linker = Linker.nativeLinker();

        // Locate the address of malloc()
        var malloc_addr = linker.defaultLookup().find("malloc").orElseThrow();

        // Create a downcall handle for malloc()
        MethodHandle malloc = linker.downcallHandle(
            malloc_addr,
            FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.JAVA_LONG)
        );

        // Invoke malloc(), which returns a pointer
        MemorySegment segment = (MemorySegment) malloc.invokeExact(byteSize);

        // The size of the memory segment created by malloc() is zero bytes!
        System.out.println(
            "Size, in bytes, of memory segment created by calling malloc.invokeExact(" +
            byteSize + "): " + segment.byteSize());

        // Localte the address of free()
        var free_addr = linker.defaultLookup().find("free").orElseThrow();

        // Create a downcall handle for free()
        MethodHandle free = linker.downcallHandle(
            free_addr,
            FunctionDescriptor.ofVoid(ValueLayout.ADDRESS)
        );


        Consumer<MemorySegment> cleanup = memorySegment -> {
            try {
                free.invokeExact(memorySegment);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };

        // This reintepret method:
        // 1. Resizes the memory segment so that it's equal to byteSize
        // 2. Associates it with an existing arena
        // 3. Invokes free() to deallocate the memory allocated by malloc()
        //    when its arena is closed

        return segment.reinterpret(byteSize, arena, cleanup);
    }

    public static void main(String[] args) {
        String s = "My string!";
        try (Arena arena = Arena.ofConfined()) {

            // Allocate off-heap memory with malloc()
            var nativeText = allocateMemory(
                ValueLayout.JAVA_CHAR.byteSize() * (s.length() + 1), arena);

            // Access off-heap memory
            for (int i = 0; i < s.length(); i++ ) {
                nativeText.setAtIndex(ValueLayout.JAVA_CHAR, i, s.charAt(i));
            }

            // Add the string terminator at the end
            nativeText.setAtIndex(
                ValueLayout.JAVA_CHAR, s.length(), Character.MIN_VALUE);

            // Print the string
            for (int i = 0; i < s.length(); i++ ) {
                System.out.print((char)nativeText.getAtIndex(ValueLayout.JAVA_CHAR, i));
            }
            System.out.println();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
