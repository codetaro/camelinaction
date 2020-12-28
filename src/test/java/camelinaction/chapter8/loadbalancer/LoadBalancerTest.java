package camelinaction.chapter8.loadbalancer;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class LoadBalancerTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .loadBalance().roundRobin()
                            .to("seda:a").to("seda:b")
                        .end();

                // service A
                from("seda:a")
                        .log("A received: ${body}")
                        .to("mock:a");

                // service B
                from("seda:b")
                        .log("B received: ${body}")
                        .to("mock:b");
            }
        };
    }

    @Test
    public void testLoadBalancer() throws Exception {
        MockEndpoint a = getMockEndpoint("mock:a");
        a.expectedBodiesReceived("Hello", "Cool");

        MockEndpoint b = getMockEndpoint("mock:b");
        b.expectedBodiesReceived("Camel rocks", "Bye");

        template.sendBody("direct:start", "Hello");
        template.sendBody("direct:start", "Camel rocks");
        template.sendBody("direct:start", "Cool");
        template.sendBody("direct:start", "Bye");

        assertMockEndpointsSatisfied();
    }
}
