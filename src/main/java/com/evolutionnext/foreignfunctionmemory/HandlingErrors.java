package com.evolutionnext.foreignfunctionmemory;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.nio.charset.StandardCharsets;

public class HandlingErrors {
    static double invokeLog(double v) throws Throwable {

        double result = Double.NaN;

        // Setup handles
        Linker.Option ccs = Linker.Option.captureCallState("errno");
        StructLayout capturedStateLayout = Linker.Option.captureStateLayout();
        VarHandle errnoHandle =
            capturedStateLayout.varHandle(MemoryLayout.PathElement.groupElement("errno"));

        // log C Standard Library function
        Linker linker = Linker.nativeLinker();
        SymbolLookup stdLib = linker.defaultLookup();
        MethodHandle log = linker.downcallHandle(
            stdLib.find("log").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.JAVA_DOUBLE, ValueLayout.JAVA_DOUBLE),
            ccs);

        // strerror C Standard Library function
        MethodHandle strerror = linker.downcallHandle(
            stdLib.find("strerror").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.JAVA_INT));

        // Actual invocation
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment capturedState = arena.allocate(capturedStateLayout);

            result = (double) log.invokeExact(capturedState, v);

            if (Double.isNaN(result)) {
                // Indicates that an error occurred per the documentation of
                // the 'log' command.

                // Get more information by consulting the value of errno:
                int errno = (int) errnoHandle.get(capturedState, 0L);

                System.out.println("errno: " + errno);

                // Convert errno code to a string message:
                String errrorString = ((MemorySegment) strerror.invokeExact(errno))
                    .reinterpret(Long.MAX_VALUE).getString(0, StandardCharsets.UTF_8);
                System.out.println("errno string: " + errrorString); // Domain error
            }
        }
        return result;
    }

    public static void main(String[] args) throws Throwable {
        System.out.println("log(2.718): " + invokeLog(2.718));
        System.out.println("log(-1): " + invokeLog(-1));
    }
}
