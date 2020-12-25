package camelinaction.chapter3;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.List;

public class PurchaseOrderCsvTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                context.setTracing(true);

                from("file://src/test/resources/camelinaction/chapter3?noop=true&fileName=order.csv")
                        .unmarshal().csv()
                        .split(body())
                        .to("mock:queue.csv");
            }
        };
    }

    @Test
    public void testCsv() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:queue.csv");
        mock.expectedMessageCount(2);

        assertMockEndpointsSatisfied();

        List line1 = mock.getReceivedExchanges().get(0).getIn().getBody(List.class);
        assertEquals("Camel in Action", line1.get(0));
        assertEquals("4995", line1.get(1));
        assertEquals("1", line1.get(2));

        List line2 = mock.getReceivedExchanges().get(1).getIn().getBody(List.class);
        assertEquals("Activemq in Action", line2.get(0));
        assertEquals("4495", line2.get(1));
        assertEquals("2", line2.get(2));
    }
}
