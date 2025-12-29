package com.epam.util;

import java.util.function.DoubleSupplier;

public class IncrementalDoubleSupplier implements DoubleSupplier {
    private double current = 0.0;

    @Override
    public double getAsDouble() {
        return current++;
    }
}
