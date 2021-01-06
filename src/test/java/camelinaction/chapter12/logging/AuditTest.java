package camelinaction.chapter12.logging;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class AuditTest extends CamelTestSupport {

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext context = super.createCamelContext();

        context.addComponent("jms", context.getComponent("seda"));
        return context;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file://data/orders")
                        .convertBodyTo(String.class)
                        .wireTap("seda:audit")
                        .bean(OrderCsvToXmlBean.class)
                        .to("jms:queue:orders");

                from("seda:audit")
                        .bean(AuditService.class, "auditFile");
            }
        };
    }

    @Test
    public void testAudit() throws Exception {
        template.sendBody("file://data/orders", "123,4444,20100110,222,1");

        String xml = consumer.receiveBody("jms:queue:orders", 5000, String.class);
        assertEquals("<order><id>123</id><customerId>4444</customerId><date>20100110</date>"
                + "<item><id>222</id><amount>1</amount></item></order>", xml);
    }
}
