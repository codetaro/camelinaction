package camelinaction.chapter5;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.CamelSpringTestSupport;
import org.apache.camel.test.CamelTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringRouteScopeTest extends CamelSpringTestSupport {

    @Override
    protected void setUp() throws Exception {
        deleteDirectory("data/orders");
        super.setUp();
    }

    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("camelinaction/chapter5/RouteScopeTest.xml");
    }

    @Test
    public void testOrderOk() throws Exception {
        MockEndpoint file = getMockEndpoint("mock:file");
        file.expectedMessageCount(1);

        MockEndpoint queue = getMockEndpoint("mock:queue.order");
        queue.expectedBodiesReceived("amount=1,name=Camel in Action,id=123,status=OK");

        template.sendBodyAndHeader("file://data/orders", "amount=1#name=Camel in Action", Exchange.FILE_NAME, "order.txt");

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testOrderActiveMQ() throws Exception {
        MockEndpoint file = getMockEndpoint("mock:file");
        file.expectedMessageCount(1);

        MockEndpoint queue = getMockEndpoint("mock:queue.order");
        queue.expectedMessageCount(0);

        template.sendBodyAndHeader("file://data/orders", "amount=1#name=ActiveMQ in Action", Exchange.FILE_NAME, "order.txt");

        Thread.sleep(10000);

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testXmlOrderFail() throws Exception {
        MockEndpoint file = getMockEndpoint("mock:file");
        file.expectedMessageCount(0);

        MockEndpoint queue = getMockEndpoint("mock:queue.order");
        queue.expectedMessageCount(0);

        template.sendBodyAndHeader("file://data/orders", "<?xml version=\"1.0\"?><order><amount>1</amount><name>Camel in Action</name></order>", Exchange.FILE_NAME, "order2.txt");

        Thread.sleep(5000);

        assertMockEndpointsSatisfied();
    }
}
