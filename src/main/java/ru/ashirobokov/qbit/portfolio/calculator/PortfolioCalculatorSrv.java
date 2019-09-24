package ru.ashirobokov.qbit.portfolio.calculator;

import io.advantageous.qbit.server.EndpointServerBuilder;
import io.advantageous.qbit.server.ServiceEndpointServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortfolioCalculatorSrv {
    public final static Logger LOG = LoggerFactory.getLogger(PortfolioCalculatorSrv.class);
    private final static PortfolioCalculatorSrv _INSTANSE = new PortfolioCalculatorSrv() ;

    private ServiceEndpointServer portfolioCalcSrvEndpointServer;
    private PortfolioCalculatorImpl portfolioCalculator;

    /** Client service proxy to the portfolioCalculatorImpl */
    private final static String portfolioCalcAddress = "portfolioCalcSrv";
    private PortfolioCalculator proxy;

    public static PortfolioCalculatorSrv getInstanse() {
        return _INSTANSE;
    }

    private PortfolioCalculatorSrv() {
        LOG.info("PortfolioCalculatorSrv...starting...");
        try {

        /* Create the EndpointServerBuilder. */
            final EndpointServerBuilder endpointServerBuilder = new EndpointServerBuilder();

        /* Create the service server. */
            portfolioCalcSrvEndpointServer = endpointServerBuilder
                    .setHealthService(null)
                    .setEnableHealthEndpoint(false)
                    .build();

        /* Create a PortfolioCalculatorImpl */
            portfolioCalculator = new PortfolioCalculatorImpl();
            // Add the portfolioCalculator to the serviceBundle.
            portfolioCalcSrvEndpointServer.serviceBundle()
                    .addServiceObject(portfolioCalcAddress, portfolioCalculator)
                    .startServiceBundle();

            //Create a proxy proxy to communicate with the service actor.
            proxy = portfolioCalcSrvEndpointServer.serviceBundle().createLocalProxy(PortfolioCalculator.class, portfolioCalcAddress);

            CalculatorHandler calculatorHandler = new CalculatorHandler(portfolioCalculator);
            calculatorHandler.init();
            calculatorHandler.calculating();

        /* Start the service endpoint server and wait until it starts. */
            portfolioCalcSrvEndpointServer.startServerAndWait();

        } catch (Exception e) {
            LOG.error("PortfolioCalculatorSrv...start error");
        }
        LOG.info("/PortfolioCalculatorSrv...started OK");

    }

    public PortfolioCalculator getProxy() {
        return proxy;
    }
}
