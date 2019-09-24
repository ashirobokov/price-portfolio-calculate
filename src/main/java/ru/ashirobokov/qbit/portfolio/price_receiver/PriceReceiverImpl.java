package ru.ashirobokov.qbit.portfolio.price_receiver;

import io.advantageous.qbit.reactive.Callback;
import io.netty.util.internal.ConcurrentSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ashirobokov.qbit.portfolio.model.InstrumentPrice;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class PriceReceiverImpl {
    private final static Logger LOG = LoggerFactory.getLogger(PriceReceiverImpl.class);
    private final ConcurrentMap<Long, String> instrumentPrices = new ConcurrentHashMap<>();
    //    private final ConcurrentNavigableMap<Long, List<InstrumentPrice>> instrumentPrices = new ConcurrentSkipListMap<>();

    public ConcurrentMap<Long, String> getInstrumentPrices() {
        return instrumentPrices;
    }

    public void addPrices(Callback<Boolean> callback, Long timestamp, List<InstrumentPrice> instrumentPrices) {
        String prices = instrumentPrices.toString();
        LOG.info("PriceReceiverSrv...PriceReceiverImpl.addPrices for timestamp {} prices {}", timestamp, instrumentPrices);
        this.instrumentPrices.put(timestamp, prices);
//        LOG.info("PriceReceiverSrv..." +
//                " RET = {} " +
//                " InstrumentPrices full map {}", ret, instrumentPrices);
        printInstrumentPrices();
        callback.accept(true);
    }

    public void getInstrPricesSize(Callback<Integer> callback) {
        LOG.info("PriceReceiverSrv...PriceReceiverImpl.getSize");
        callback.accept(instrumentPrices.size());
    }

    public void printInstrumentPrices() {
        LOG.info("PriceReceiverSrv... InstrumentPrices full map");
        instrumentPrices.forEach((timestamp, list) -> {
            LOG.info(".... timestamp = {} .... list {}", timestamp, list);
        });
    }

/*
    public void printInstrumentPricesEntries() {
        LOG.info("PriceReceiverSrv..." +
                " InstrumentPrices full map [Entry Set]");
        Set<Map.Entry<Long, List<InstrumentPrice>>> entrySet = instrumentPrices.entrySet();
        for (Map.Entry<Long, List<InstrumentPrice>> entry : entrySet) {
            LOG.info(".... timestamp = {} .... list {}", entry.getKey(), entry.getValue());
        }
    }
*/
}
