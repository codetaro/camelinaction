package camelinaction.chapter3;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class PurchaseOrderVelocityTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:mail")
                        .setHeader("Subject", constant("Thanks for ordering"))
                        .setHeader("From", constant("donotreply@riders.com"))
                        .to("velocity://camelinaction.chapter3/email.vm")
                        .to("log:mail")
                        .to("mock:mail");
            }
        };
    }

    @Test
    public void testVelocity() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:mail");
        mock.expectedMessageCount(1);
        mock.message(0).header("Subject").isEqualTo("Thanks for ordering");
        mock.message(0).header("From").isEqualTo("donotreply@riders.com");
        mock.message(0).body().contains("Dear customer\n" +
                "\n" +
                "Thank you for ordering 1.0 pieces(s) of Camel in Action at a cost of 4995.0.\n" +
                "\n" +
                "This is an automated email, please do not reply.");

        PurchaseOrderXml order = new PurchaseOrderXml();
        order.setName("Camel in Action");
        order.setPrice(4995);
        order.setAmount(1);

        template.sendBody("direct:mail", order);

        assertMockEndpointsSatisfied();
    }
}
