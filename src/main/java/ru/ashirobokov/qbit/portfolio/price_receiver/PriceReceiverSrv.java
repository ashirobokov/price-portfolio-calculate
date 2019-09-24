package ru.ashirobokov.qbit.portfolio.price_receiver;

import io.advantageous.qbit.server.EndpointServerBuilder;
import io.advantageous.qbit.server.ServiceEndpointServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PriceReceiverSrv {
    public final static Logger LOG = LoggerFactory.getLogger(PriceReceiverSrv.class);
    private final static PriceReceiverSrv _INSTANSE = new PriceReceiverSrv();

    private ServiceEndpointServer priceRcvSrvEndpointServer;
    private PriceReceiverImpl priceReceiver;

    /**
     * Client service proxy to the PriceReceiverImpl
     */
    private final static String priceRcvAddress = "priceRcvSrv";
    private PriceReceiver proxy;

    public static PriceReceiverSrv getInstanse() {
        return _INSTANSE;
    }

    private PriceReceiverSrv() {
        LOG.info("PriceReceiverSrv...starting...");
        try {

        /* Create the EndpointServerBuilder. */
            final EndpointServerBuilder endpointServerBuilder = new EndpointServerBuilder();

        /* Create the service server. */
            priceRcvSrvEndpointServer = endpointServerBuilder
                    .setHealthService(null)
                    .setEnableHealthEndpoint(false)
                    .build();

        /* Create a PriceReceiverImpl */
            priceReceiver = new PriceReceiverImpl();
            // Add the priceReceiver to the serviceBundle.
            priceRcvSrvEndpointServer.serviceBundle()
                    .addServiceObject(priceRcvAddress, priceReceiver)
                    .startServiceBundle();

            //Create a proxy to communicate with the service actor.
            proxy = priceRcvSrvEndpointServer.serviceBundle().createLocalProxy(PriceReceiver.class, priceRcvAddress);

            //Create a handler to filter and process received prices
//            PriceHandler priceHandler = new PriceHandler(priceReceiver);
//            priceHandler.processing();

        /* Start the service endpoint server and wait until it starts. */
            priceRcvSrvEndpointServer.startServerAndWait();

        } catch (Exception e) {
            LOG.error("PriceReceiverSrv...start error");
        }
        LOG.info("/PriceReceiverSrv...started OK");
    }

    public PriceReceiver getProxy() {
        return proxy;
    }
}
