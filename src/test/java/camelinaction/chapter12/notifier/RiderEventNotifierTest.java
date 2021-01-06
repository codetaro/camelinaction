package camelinaction.chapter12.notifier;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.net.ConnectException;

import static org.junit.Assert.*;

public class RiderEventNotifierTest extends CamelTestSupport {

    private DummyRiderFailurePublisher dummy;

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext context = super.createCamelContext();

        dummy = new DummyRiderFailurePublisher();

        RiderEventNotifier notifier = new RiderEventNotifier("MyRider");
        notifier.setPublisher(dummy);
        context.getManagementStrategy().addEventNotifier(notifier);

        return context;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:ok").to("mock:ok");
                from("direct:fail").throwException(new ConnectException("Simulated exception"));
            }
        };
    }

    @Test
    public void testOk() throws Exception {
        getMockEndpoint("mock:ok").expectedMessageCount(1);

        template.sendBody("direct:ok", "Camel rocks");

        assertMockEndpointsSatisfied();

        assertEquals(0, dummy.getEvents().size());
    }

    @Test
    public void testFailure() throws Exception {
        try {
            template.sendBody("direct:fail", "This should fail");
        } catch (Exception e) {
            // ignore for now
        }

        assertEquals(1, dummy.getEvents().size());
        DummyRiderFailurePublisher.Event event = dummy.getEvents().get(0);
        assertEquals("MyRider", event.appId);
        assertNotNull(event.eventId);
        assertNotNull(event.timestamp);
        assertEquals("Simulated exception", event.message);
    }
}