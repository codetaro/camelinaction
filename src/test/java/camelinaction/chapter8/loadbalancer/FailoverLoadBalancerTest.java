package camelinaction.chapter8.loadbalancer;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class FailoverLoadBalancerTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .loadBalance().failover()
                            .to("direct:a").to("direct:b")
                        .end();

                from("direct:a")
                        .log("A received: ${body}")
                        .choice()
                            .when(body().contains("Kaboom"))
                                .throwException(new IllegalArgumentException("Damn"))
                            .end()
                        .end()
                        .to("mock:a");

                from("direct:b")
                        .log("B received: ${body}")
                        .to("mock:b");
            }
        };
    }

    @Test
    public void testLoadBalancer() throws Exception {
        MockEndpoint a = getMockEndpoint("mock:a");
        a.expectedBodiesReceived("Hello", "Cool", "Bye");

        MockEndpoint b = getMockEndpoint("mock:b");
        b.expectedBodiesReceived("Kaboom");

        template.sendBody("direct:start", "Hello");
        template.sendBody("direct:start", "Kaboom");
        template.sendBody("direct:start", "Cool");
        template.sendBody("direct:start", "Bye");

        assertMockEndpointsSatisfied();
    }
}
