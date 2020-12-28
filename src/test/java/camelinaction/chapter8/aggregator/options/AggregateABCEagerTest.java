package camelinaction.chapter8.aggregator.options;

import camelinaction.chapter8.aggregator.MyEndAggregationStrategy;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class AggregateABCEagerTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .log("Sending ${body} with correlation key ${header.myId}")
                        .aggregate(header("myId"), new MyEndAggregationStrategy())
                            .completionPredicate(body().isEqualTo("END")).eagerCheckCompletion()
                            .log("Sending out ${body}")
                            .to("mock:result");
            }
        };
    }

    @Test
    public void testABCEND() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("ABC");

        template.sendBodyAndHeader("direct:start", "A", "myId", 1);
        template.sendBodyAndHeader("direct:start", "B", "myId", 1);
        template.sendBodyAndHeader("direct:start", "F", "myId", 2);
        template.sendBodyAndHeader("direct:start", "C", "myId", 1);
        // the END message trigger completion
        template.sendBodyAndHeader("direct:start", "END", "myId", 1);

        assertMockEndpointsSatisfied();
    }
}
