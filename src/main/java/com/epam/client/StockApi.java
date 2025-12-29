package com.epam.client;

import java.util.stream.DoubleStream;

public interface StockApi {

    DoubleStream getPrices(String stock);
}
