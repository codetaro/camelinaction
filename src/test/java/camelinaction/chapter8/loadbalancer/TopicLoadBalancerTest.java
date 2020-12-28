package camelinaction.chapter8.loadbalancer;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class TopicLoadBalancerTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .loadBalance().topic()
                            // this is the 2 processors which we will balance across
                            .to("seda:a").to("seda:b")
                        .end();

                from("seda:a")
                        .log("A received: ${body}")
                        .to("mock:a");

                from("seda:b")
                        .log("B received: ${body}")
                        .to("mock:b");
            }
        };
    }

    @Test
    public void testLoadBalancer() throws Exception {
        MockEndpoint a = getMockEndpoint("mock:a");
        a.expectedMessageCount(4);

        MockEndpoint b = getMockEndpoint("mock:b");
        b.expectedMessageCount(4);

        template.sendBody("direct:start", "Hello");
        template.sendBody("direct:start", "Camel rocks");
        template.sendBody("direct:start", "Cool");
        template.sendBody("direct:start", "Bye");

        assertMockEndpointsSatisfied();
    }
}
