package com.evolutionnext.foreignfunctionmemory;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class InvokeStrLen {
    static long invokeStrlen(String s) throws Throwable {

        try (Arena arena = Arena.ofConfined()) {

            // Allocate off-heap memory and
            // copy the argument, a Java string, into off-heap memory
            MemorySegment nativeStringSegment = arena.allocateFrom(s);

            String nativeString = nativeStringSegment.getString(0);
            System.out.printf("Retrieve the string from native off-head memory: %s%n", nativeString);

            // Link and call the C function strlen

            // Obtain an instance of the native linker
            Linker linker = Linker.nativeLinker();

            // Locate the address of the C function signature
            SymbolLookup stdLib = linker.defaultLookup();
            MemorySegment strlen_addr = stdLib.find("strlen").get();

            // Create a description of the C function
            // The first argument of the `FunctionDescriptor::of` method
            //   is the layout of the native function's return value.
            // Native primitive types are modeled using value layouts whose size matches that of such types.
            // This means that a function descriptor is platform-specific.
            // For example, size_t has a layout of JAVA_LONG on 64-bit or x64 platforms
            //   but a layout of JAVA_INT on 32-bit or x86 platforms
            // Given `strlen` is the following in C: size_t strlen(const char *s);
            // You can call `Map<String,MemoryLayout> canonicalLayouts()` from the linker to get the type layouts for your platform

            linker.canonicalLayouts()
                .forEach((key, value) ->
                    System.out.printf("Memory: %s, Bytes %d%n", key, value.byteSize()));

            FunctionDescriptor strlen_sig =
                FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS);

            // Create a downcall handle for the C function
            MethodHandle strlen = linker.downcallHandle(strlen_addr, strlen_sig);

            // Call the C function directly from Java, cast because it returns an Object
            return (long)strlen.invokeExact(nativeStringSegment);
        }
    }

    public static void main(String[] args) throws Throwable {
        long lengthFromC = invokeStrlen("Hello");
        System.out.printf("The final length from strlen is %s", lengthFromC);
    }
}
