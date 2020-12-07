package camelinaction.chapter5;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.CamelTestSupport;
import org.junit.Test;

public class RouteScopeTest extends CamelTestSupport {

    @Override
    protected void setUp() throws Exception {
        deleteDirectory("data/orders");
        super.setUp();
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        // register order service bean in the Camel registry
        JndiRegistry jndi = super.createRegistry();
        jndi.bind("orderService", new OrderService());
        return jndi;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // this is the default error handler which is context scoped
                errorHandler(defaultErrorHandler()
                        .maximumRedeliveries(2)
                        .redeliveryDelay(1000)
                        .retryAttemptedLogLevel(LoggingLevel.WARN));

                from("file://data/orders?delay=10000")
                        .beanRef("orderService", "toCsv")
                        .to("mock:file")
                        .to("seda:queue.inbox");

                // 2nd route has a route scoped DeadLetterChannel as its error handler
                from("seda:queue.inbox")
                        .errorHandler(deadLetterChannel("log:DLC")
                                .maximumRedeliveries(5)
                                .retryAttemptedLogLevel(LoggingLevel.INFO)
                                .redeliveryDelay(250)
                                .backOffMultiplier(2))
                        .beanRef("orderService", "validate")
                        .beanRef("orderService", "enrich")
                        .to("mock:queue.order");
            }
        };
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
