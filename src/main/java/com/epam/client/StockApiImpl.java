package com.epam.client;

import com.epam.service.NumberSequenceGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.DoubleStream;

public class StockApiImpl implements StockApi {
    private final Map<String, DoubleStream> CACHE = new HashMap<>();
    @Override
    public DoubleStream getPrices(String stock) {
        return CACHE.computeIfAbsent(stock, s -> NumberSequenceGenerator.getSequence());
    }
}
