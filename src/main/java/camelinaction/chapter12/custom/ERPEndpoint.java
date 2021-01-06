package camelinaction.chapter12.custom;

import org.apache.camel.*;
import org.apache.camel.api.management.ManagedAttribute;
import org.apache.camel.api.management.ManagedResource;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.ManagementAware;

// Exposes class as MBean
@ManagedResource(description = "Managed ERPEndpoint")
public class ERPEndpoint extends DefaultEndpoint
    implements ManagementAware<ERPEndpoint> {

    private boolean verbose;

    public ERPEndpoint(String endpointUri, Component component) {
        super(endpointUri, component);
    }

    @Override
    public Producer createProducer() throws Exception {
        return new ERPProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("Consumer not supported");
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    // Exposes attributes for management
    @ManagedAttribute
    public boolean isVerbose() {
        return verbose;
    }

    // Exposes attributes for management
    @ManagedAttribute
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    // Tells Camel to use this MBean
    @Override
    public Object getManagedObject(ERPEndpoint object) {
        return this;
    }
}
