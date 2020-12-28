package camelinaction.chapter8.aggregator;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringAggregateABCTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("camelinaction/chapter8/aggregate-abc.xml");
    }

    @Test
    public void testABC() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("ABC", "FGH");

        template.sendBodyAndHeader("direct:start", "A", "myId", 1);
        template.sendBodyAndHeader("direct:start", "B", "myId", 1);
        template.sendBodyAndHeader("direct:start", "F", "myId", 2);
        template.sendBodyAndHeader("direct:start", "C", "myId", 1);

        template.sendBodyAndHeader("direct:start", "G", "myId", 2);
        template.sendBodyAndHeader("direct:start", "H", "myId", 2);

        assertMockEndpointsSatisfied();
    }
}
