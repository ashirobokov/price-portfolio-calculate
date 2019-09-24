package ru.ashirobokov.qbit.portfolio.calculator;

import io.advantageous.qbit.reactive.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ashirobokov.qbit.portfolio.model.InstrumentPrice;
import ru.ashirobokov.qbit.portfolio.model.Portfolio;

import java.util.List;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class PortfolioCalculatorImpl {
    private final static Logger LOG = LoggerFactory.getLogger(PortfolioCalculatorImpl.class);
    private final ConcurrentNavigableMap<Long, List<InstrumentPrice>> instrumentPrices = new ConcurrentSkipListMap<>();
    private final CopyOnWriteArrayList<Portfolio> portfolios = new CopyOnWriteArrayList<>();

    public void sendPrices(Callback<Boolean> callback, Long timestamp, List<InstrumentPrice> prices) {
        LOG.info("PortfolioCalculatorSrv..." +
                "PortfolioCalculatorImpl.sendPrices for timestamp {} price values {}", timestamp, prices.toString());
        instrumentPrices.putIfAbsent(timestamp, prices);
        LOG.info("PortfolioCalculatorSrv..." +
                "InstrumentPrices full map {}", instrumentPrices);
        callback.accept(true);
    }

    public ConcurrentNavigableMap<Long, List<InstrumentPrice>> getInstrumentPrices() {
        return instrumentPrices;
    }

    public CopyOnWriteArrayList<Portfolio> getPortfolios() {
        return portfolios;
    }
}
