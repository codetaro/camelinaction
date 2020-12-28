package camelinaction.chapter8.routingslip;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class RoutingSlipBeanTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .bean(SlipBean.class);
            }
        };
    }

    @Test
    public void testRoutingSlip() throws Exception {
        getMockEndpoint("mock:a").expectedMessageCount(1);
        getMockEndpoint("mock:b").expectedMessageCount(0);
        getMockEndpoint("mock:c").expectedMessageCount(1);

        template.sendBody("direct:start", "Hello World");

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRoutingSlipCool() throws Exception {
        getMockEndpoint("mock:a").expectedMessageCount(1);
        getMockEndpoint("mock:b").expectedMessageCount(1);
        getMockEndpoint("mock:c").expectedMessageCount(1);

        template.sendBody("direct:start", "We are Cool");

        assertMockEndpointsSatisfied();
    }
}
