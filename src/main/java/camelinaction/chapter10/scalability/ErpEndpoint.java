package camelinaction.chapter10.scalability;

import org.apache.camel.*;
import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.impl.DefaultEndpoint;

public class ErpEndpoint extends DefaultEndpoint {

    public ErpEndpoint(String uri, Component component) {
        super(uri, component);
    }

    public boolean isSingleton() {
        return true;
    }


    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw  new UnsupportedOperationException("ErpComponent does not support consumer");
    }

    @Override
    public Producer createProducer() throws Exception {
        return new ErpProducer(this);
    }
}
