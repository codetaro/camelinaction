package camelinaction.chapter8.dynamicrouter;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringDynamicRouterTest extends CamelSpringTestSupport {
    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("camelinaction/chapter8/dynamicrouter.xml");
    }

    @Test
    public void testDynamicRouter() throws Exception {
        getMockEndpoint("mock:a").expectedBodiesReceived("Camel");
        getMockEndpoint("mock:result").expectedBodiesReceived("Bye Camel");

        template.sendBody("direct:start", "Camel");

        assertMockEndpointsSatisfied();
    }
}
