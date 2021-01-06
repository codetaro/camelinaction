package camelinaction.chapter12.notifier;

import org.apache.camel.management.event.ExchangeFailedEvent;
import org.apache.camel.support.EventNotifierSupport;

import java.util.Date;
import java.util.EventObject;

public class RiderEventNotifier extends EventNotifierSupport {

    private RiderFailurePublisher publisher;
    private String appId;

    public RiderEventNotifier(String appId) {
        this.appId = appId;
    }

    @Override
    public void notify(EventObject eventObject) throws Exception {
        if (eventObject instanceof ExchangeFailedEvent) {
            notifyFailure((ExchangeFailedEvent) eventObject);
        }
    }

    protected void notifyFailure(ExchangeFailedEvent event) {
        String id = event.getExchange().getExchangeId();
        Exception cause = event.getExchange().getException();
        Date now = new Date();

        publisher.publish(appId, id, now, cause.getMessage());
    }

    @Override
    public boolean isEnabled(EventObject eventObject) {
        return true;
    }

    public void setPublisher(RiderFailurePublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    protected void doStart() throws Exception {
        // here you can initialize services etc
    }

    @Override
    protected void doStop() throws Exception {
        // here you can cleanup services
    }
}
