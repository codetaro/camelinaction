package camelinaction.chapter4;

import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Document;

public class XmlOrderTest extends CamelSpringTestSupport {

    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("camelinaction/chapter4/xmlOrder.xml");
    }

    @Override
    public void setUp() throws Exception {
        deleteDirectory("target/order");
        super.setUp();
    }

    @Test
    public void testSendIncomingOrder() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:queue:order");
        mock.expectedMessageCount(1);

        // prepare a XML document from a String which is converted to a DOM
        String body = "<order customerId='4444'><item>Camel in action</item></order>";
        Document xml = context.getTypeConverter().convertTo(Document.class, body);

        // store the order as a file which is picked up by the route
        template.sendBodyAndHeader("file://data/orders", xml, Exchange.FILE_NAME, "order.xml");

        mock.assertIsSatisfied();
    }
}
