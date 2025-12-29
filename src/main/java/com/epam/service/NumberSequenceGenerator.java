package com.epam.service;

import com.epam.util.IncrementalDoubleSupplier;

import java.util.stream.DoubleStream;

public class NumberSequenceGenerator {
    public static DoubleStream getSequence() {
        return DoubleStream.generate(new IncrementalDoubleSupplier());
    }
}
