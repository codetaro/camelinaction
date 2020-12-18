package camelinaction.chapter6;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class FirstMockTest extends CamelTestSupport {
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("jms:topic:quote").to("mock:quote");
            }
        };
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext context = super.createCamelContext();
        context.addComponent("jms", context.getComponent("seda"));
        return context;
    }

    @Test
    public void testQuote() throws Exception {
        MockEndpoint quote = getMockEndpoint("mock:quote");
        quote.setExpectedMessageCount(2);
        List bodies = Arrays.asList("Camel rocks", "Hello Camel");
        quote.expectedBodiesReceived(bodies);

        template.sendBody("jms:topic:quote", "Camel rocks");
        template.sendBody("jms:topic:quote", "Hello Camel");

        quote.assertIsSatisfied();
    }

    @Test
    public void testIsCamelMessage() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:quote");
        mock.expectedMessageCount(2);
        mock.message(0).body().contains("Camel");
        mock.message(1).body().contains("Camel");

        template.sendBody("jms:topic:quote", "Hello Camel");
        template.sendBody("jms:topic:quote", "Camel rocks");

        assertMockEndpointsSatisfied();

//        List<Exchange> list = mock.getReceivedExchanges();
//        String body1 = list.get(0).getIn().getBody(String.class);
//        String body2 = list.get(1).getIn().getBody(String.class);
//
//        assertTrue(body1.contains("Camel"));
//        assertTrue(body2.contains("Camel"));
    }

    @Test
    public void testGap() throws Exception {
        final MockEndpoint mock = getMockEndpoint("mock:quote");
        mock.expectedMessageCount(3);
        mock.expects(new Runnable() {
            public void run() {
                int last = 0;
                for (Exchange exchange : mock.getExchanges()) {
                    int current = exchange.getIn().getHeader("Counter", Integer.class);
                    if (current <= last) {
                        fail("Counter is not greater than last counter");
                    } else if (current - last != 1) {
                        fail("Gap detected: last: " + last
                        + " current: " + current);
                    }
                    last = current;
                }
            }
        });

        template.sendBodyAndHeader("jms:topic:quote", "A", "Counter", 1);
        template.sendBodyAndHeader("jms:topic:quote", "B", "Counter", 2);
        template.sendBodyAndHeader("jms:topic:quote", "C", "Counter", 4);

        mock.assertIsNotSatisfied();
    }
}
