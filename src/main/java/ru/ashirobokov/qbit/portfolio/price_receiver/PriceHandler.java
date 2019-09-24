package ru.ashirobokov.qbit.portfolio.price_receiver;

import io.advantageous.qbit.reactive.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ashirobokov.qbit.portfolio.calculator.PortfolioCalculator;
import ru.ashirobokov.qbit.portfolio.calculator.PortfolioCalculatorSrv;
import ru.ashirobokov.qbit.portfolio.model.InstrumentPrice;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.SECONDS;

public class PriceHandler {
    private final Logger LOG = LoggerFactory.getLogger(PriceHandler.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final PriceReceiverImpl priceReceiver;
    private final TreeSet<Long> calculatedTimestamps = new TreeSet<>();
    private final List<InstrumentPrice> previousPrices = new ArrayList<>();
//    private final PortfolioCalculator proxy = PortfolioCalculatorSrv.getInstanse().getProxy();

    private Callback<Boolean> callbackSendPrices =  new Callback<Boolean>() {
        @Override
        public void accept(Boolean aBoolean) {
            LOG.info("[Callback<Boolean>.sendPrices] to PortfolioCalculatorSrv...{}", aBoolean.booleanValue() ? "sent OK" : "failed" );
        }
    };

    public PriceHandler(PriceReceiverImpl priceReceiver) {
        this.priceReceiver = priceReceiver;
    }

    /**
     *
     * @param currentPrices
     * @return
     */
    private List<InstrumentPrice> comparing(final List<InstrumentPrice> currentPrices) {
        LOG.info("1......comparing ... current {} vs previous {}", currentPrices, previousPrices);
        final List<InstrumentPrice> filteredPrices = currentPrices.stream()
                        .filter(current -> {
                                Optional<InstrumentPrice> previous = previousPrices.stream().filter(p -> p.getInstrumentId() == current.getInstrumentId()).findFirst();
                                if (previous.isPresent()) {
                                    int index = previousPrices.indexOf(previous.get());
                                    return current.getPrice()
                                            .compareTo(previousPrices.get(index).getPrice()) != 0;
                                } else {
                                    return true;
                                }
                            })
                        .collect(Collectors.toList());
        LOG.info("PriceHandler...comparing...filteredPrices size {}", filteredPrices.size());
        filteredPrices.forEach(filtered -> {
            Optional<InstrumentPrice> previous = previousPrices.stream().filter(p -> p.getInstrumentId() == filtered.getInstrumentId()).findFirst();
            if (previous.isPresent()) {
                int index = previousPrices.indexOf(previous.get());
                previousPrices.set(index, filtered);
            } else {
                previousPrices.add(filtered);
            }
        });
        LOG.info("2......comparing ... previous {}", previousPrices);
    return filteredPrices;
    }

    public void processing() {
        LOG.info("PriceHandler...price processing...");
        final Runnable processor = new Runnable() {
            public void run() {
//                ConcurrentNavigableMap<Long, List<InstrumentPrice>> processedPrices =
//                            calculatedTimestamps.isEmpty() ? priceReceiver.getInstrumentPrices() :
//                                        priceReceiver.getInstrumentPrices().tailMap(calculatedTimestamps.last(), false);
//                ConcurrentMap<Long, List<InstrumentPrice>> processedPrices = priceReceiver.getInstrumentPrices();
                ConcurrentMap<Long, String> processedPrices = priceReceiver.getInstrumentPrices();
                LOG.info("PriceHandler...processed Prices = {}", processedPrices);
                processedPrices.forEach((timestamp, list) -> {

/** TODO
        debug private comparing(list) method and send sendPrices instead of list;
*/
                            calculatedTimestamps.add(timestamp);
//                            List<InstrumentPrice> sendPrices = comparing(list);
//                            proxy.sendPrices(callbackSendPrices, timestamp, list);
                });
                LOG.info("PriceHandler...calculatedTimestamps set {}", calculatedTimestamps);
            }
        };
        final ScheduledFuture<?> processorHandler =
                scheduler.scheduleAtFixedRate(processor, 20, 10, SECONDS);
        scheduler.schedule(new Runnable() {
            public void run() {
                processorHandler.cancel(true);
            }
        }, 60 * 1, SECONDS);
    }

}
