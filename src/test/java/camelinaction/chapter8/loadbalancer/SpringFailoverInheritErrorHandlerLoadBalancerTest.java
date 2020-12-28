package camelinaction.chapter8.loadbalancer;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringFailoverInheritErrorHandlerLoadBalancerTest extends CamelSpringTestSupport {
    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("camelinaction/chapter8/failover-inheriterrorhandler-loadbalancer.xml");
    }

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Test
    public void testLoadBalancer() throws Exception {
        context.getRouteDefinition("start").adviceWith(context, new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("direct:a")
                        .choice()
                            .when(body().contains("Kaboom"))
                                .throwException(new IllegalArgumentException("Damn"))
                            .end()
                        .end();
            }
        });
        context.start();

        MockEndpoint a = getMockEndpoint("mock:a");
        a.expectedBodiesReceived("Hello");

        MockEndpoint b = getMockEndpoint("mock:b");
        b.expectedBodiesReceived("Kaboom");

        template.sendBody("direct:start", "Hello");
        template.sendBody("direct:start", "Kaboom");

        assertMockEndpointsSatisfied();
    }
}
