package camelinaction.chapter8.aggregator.options;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.List;

public class AggregateABCGroupTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .log("Sending ${body} with correlation key ${header.myId}")
                        .aggregate(header("myId"))
                            .completionSize(3).groupExchanges()
                            .log("Sending out ${body}")
                            .to("mock:result");
            }
        };
    }

    @Test
    public void testABCGroup() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.message(0).property(Exchange.GROUPED_EXCHANGE).isInstanceOf(List.class);

        template.sendBodyAndHeader("direct:start", "A", "myId", 1);
        template.sendBodyAndHeader("direct:start", "B", "myId", 1);
        template.sendBodyAndHeader("direct:start", "F", "myId", 2);
        template.sendBodyAndHeader("direct:start", "C", "myId", 1);

        assertMockEndpointsSatisfied();

        Exchange exchange = mock.getExchanges().get(0);

        List list = exchange.getProperty(Exchange.GROUPED_EXCHANGE, List.class);
        assertEquals("Should contain the 3 arrived exchanges", 3, list.size());

        Exchange a = (Exchange) list.get(0);
        assertEquals("A", a.getIn().getBody());
        Exchange b = (Exchange) list.get(1);
        assertEquals("B", b.getIn().getBody());
        Exchange c = (Exchange) list.get(2);
        assertEquals("C", c.getIn().getBody());
    }
}
