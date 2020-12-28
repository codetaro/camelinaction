package camelinaction.chapter8.dynamicrouter;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class DynamicRouterAnnotationTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
//                        .dynamicRouter(method(DynamicRouterBean.class, "route"))
                        .bean(DynamicRouterBean.class, "route")
                        .to("mock:result");
            }
        };
    }

    @Test
    public void testDynamicRouterAnnotation() throws Exception {
        getMockEndpoint("mock:a").expectedBodiesReceived("Camel");
        getMockEndpoint("mock:result").expectedBodiesReceived("Bye Camel");

        template.sendBody("direct:start", "Camel");

        assertMockEndpointsSatisfied();
    }
}
