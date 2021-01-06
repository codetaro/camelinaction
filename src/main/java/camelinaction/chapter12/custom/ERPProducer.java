package camelinaction.chapter12.custom;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultProducer;

public class ERPProducer extends DefaultProducer {

    public ERPProducer(ERPEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    public ERPEndpoint getEndpoint() {
        return (ERPEndpoint) super.getEndpoint();
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String input = exchange.getIn().getBody(String.class);

        if (getEndpoint().isVerbose()) {
            System.out.println("Calling ERP with: " + input);
        }

        exchange.getOut().setBody("Simulated response from ERP");
        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
    }
}
