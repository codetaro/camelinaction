package camelinaction.chapter8.splitter;

import camelinaction.chapter8.aggregator.MyAggregationStrategy;
import org.apache.camel.CamelExchangeException;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class SplitterStopOnExceptionABCTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .split(body(), new MyAggregationStrategy())
                            .stopOnException()
                            .log("Split line ${body}")
                            .bean(WordTranslateBean.class)
                            .to("mock:split")
                        .end()
                        .log("Aggregated ${body}")
                        .to("mock:result");
            }
        };
    }

    @Test
    public void testSplitAggregateABC() throws Exception {
        MockEndpoint split = getMockEndpoint("mock:split");
        split.expectedBodiesReceived("Camel rocks");

        MockEndpoint result = getMockEndpoint("mock:result");
        result.expectedMessageCount(0);

        try {
            template.sendBody("direct:start", "A,F,C");
            fail("Should have thrown an exception");
        } catch (CamelExecutionException e) {
            CamelExchangeException cause = assertIsInstanceOf(CamelExchangeException.class, e.getCause());
            IllegalArgumentException iae = assertIsInstanceOf(IllegalArgumentException.class, cause.getCause());
            assertEquals("Key not a known word F", iae.getMessage());
        }

        assertMockEndpointsSatisfied();
    }
}
