package camelinaction.chapter10.scalability;

import org.apache.camel.AsyncCallback;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultAsyncProducer;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class ErpProducer extends DefaultAsyncProducer {

    // use a thread pool for async communication with ERP
    private ExecutorService executor;

    public ErpProducer(Endpoint endpoint) {
        super(endpoint);
        // use Camel to create the thread pool for us
        executor = endpoint.getCamelContext()
                .getExecutorServiceManager()
                .newFixedThreadPool(this, "ERP", 10);
    }

    @Override
    public boolean process(final Exchange exchange,
                           final AsyncCallback callback) {
        // simulate async communication
        executor.submit(new ERPTask(exchange, callback));

        // return false to tell Camel that we process asynchronously
        log.info("Returning false");
        return false;
    }

    private class ERPTask implements Runnable {

        private final Exchange exchange;
        private final AsyncCallback callback;

        public ERPTask(Exchange exchange, AsyncCallback callback) {
            this.exchange = exchange;
            this.callback = callback;
        }

        @Override
        public void run() {
            log.info("Calling ERP");
            // simulate communication with ERP takes 5 seconds
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // ignore
            }
            log.info("ERP reply received");

            // set reply
            String in = exchange.getIn().getBody(String.class);
            exchange.getOut().setBody(in + ";516");

            // notify callback we are done
            // we must use done(false) because the process method returned false
            log.info("Continue routing");
            callback.done(false);
        }
    }
}
