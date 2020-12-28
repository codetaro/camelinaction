package camelinaction.chapter8.splitter;

import camelinaction.chapter8.aggregator.MyAggregationStrategy;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class SplitterAggregateABCTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .split(body(), new MyAggregationStrategy())
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
        split.expectedBodiesReceived("Camel rocks", "Hi mom", "Yes it works");

        MockEndpoint result = getMockEndpoint("mock:result");
        result.expectedBodiesReceived("Camel rocks" + "Hi mom" + "Yes it works");

        template.sendBody("direct:start", "A,B,C");

        assertMockEndpointsSatisfied();
    }
}
