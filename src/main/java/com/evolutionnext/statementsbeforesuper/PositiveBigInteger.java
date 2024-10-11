package com.evolutionnext.statementsbeforesuper;

import java.math.BigInteger;

public class PositiveBigInteger extends BigInteger {
    public PositiveBigInteger(long value) {
        BigInteger bigInteger = BigInteger.valueOf(value);
        if (bigInteger.longValue() <= 0) {
            throw new IllegalArgumentException("non-positive value");
        }
        super(bigInteger.toString());
    }
}
