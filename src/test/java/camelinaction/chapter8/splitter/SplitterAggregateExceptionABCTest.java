package camelinaction.chapter8.splitter;

import camelinaction.chapter8.aggregator.MyIgnoreFailureAggregationStrategy;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class SplitterAggregateExceptionABCTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .split(body(), new MyIgnoreFailureAggregationStrategy())
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
    public void testSplitAggregateExceptionABC() throws Exception {
        MockEndpoint split = getMockEndpoint("mock:split");
        split.expectedBodiesReceived("Camel rocks", "Yes it works");

        MockEndpoint result = getMockEndpoint("mock:result");
        result.expectedBodiesReceived("Camel rocks+Yes it works");

        template.sendBody("direct:start", "A,F,C");

        assertMockEndpointsSatisfied();
    }
}
