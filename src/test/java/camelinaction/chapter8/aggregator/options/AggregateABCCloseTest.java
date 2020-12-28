package camelinaction.chapter8.aggregator.options;

import camelinaction.chapter8.aggregator.MyAggregationStrategy;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.processor.aggregate.ClosedCorrelationKeyException;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class AggregateABCCloseTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .log("Sending ${body} with correlation key ${header.myId}")
                        .aggregate(header("myId"), new MyAggregationStrategy())
                            .completionSize(3).closeCorrelationKeyOnCompletion(2000)
                            .log("Sending out ${body}")
                            .to("mock:result");
            }
        };
    }

    @Test
    public void testABCClose() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("ABC");

        template.sendBodyAndHeader("direct:start", "A", "myId", 1);
        template.sendBodyAndHeader("direct:start", "B", "myId", 1);
        template.sendBodyAndHeader("direct:start", "F", "myId", 2);
        template.sendBodyAndHeader("direct:start", "C", "myId", 1);

        try {
            template.sendBodyAndHeader("direct:start", "A2", "myId", 1);
        } catch (CamelExecutionException e) {
            ClosedCorrelationKeyException cause = assertIsInstanceOf(ClosedCorrelationKeyException.class, e.getCause());
            assertEquals("1", cause.getCorrelationKey());
        }

        assertMockEndpointsSatisfied();
    }
}
