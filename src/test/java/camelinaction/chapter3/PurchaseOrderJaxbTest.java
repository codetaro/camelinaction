package camelinaction.chapter3;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PurchaseOrderJaxbTest extends CamelSpringTestSupport {
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("camelinaction/chapter3/order-jaxb.xml");
    }

    @Test
    public void testJaxb() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:order");
        mock.expectedMessageCount(1);
        mock.message(0).body().isInstanceOf(PurchaseOrderXml.class);

        PurchaseOrderXml order = new PurchaseOrderXml();
        order.setName("Camel in Action");
        order.setPrice(4995);
        order.setAmount(1);

        template.sendBody("direct:order", order);

        assertMockEndpointsSatisfied();
    }
}
