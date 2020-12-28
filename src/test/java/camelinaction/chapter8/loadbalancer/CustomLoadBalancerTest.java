package camelinaction.chapter8.loadbalancer;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class CustomLoadBalancerTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .loadBalance(new MyCustomLoadBalancer())
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
        a.expectedBodiesReceived("Camel rocks", "Cool");

        MockEndpoint b = getMockEndpoint("mock:b");
        b.expectedBodiesReceived("Hello", "Bye");

        template.sendBodyAndHeader("direct:start", "Hello", "type", "silver");
        template.sendBodyAndHeader("direct:start", "Camel rocks", "type", "gold");
        template.sendBodyAndHeader("direct:start", "Cool", "type", "gold");
        template.sendBodyAndHeader("direct:start", "Bye", "type", "bronze");

        assertMockEndpointsSatisfied();
    }
}
