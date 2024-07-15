package com.evolutionnext.statementsbeforesuper;

import java.math.BigInteger;

public class PositiveBigInteger extends BigInteger {
    public PositiveBigInteger(long value) {
        super(BigInteger.valueOf(value).toString());
        if (BigInteger.valueOf(value).longValue() <= 0)
            throw new IllegalArgumentException("non-positive value");
    }
}
