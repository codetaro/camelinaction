package camelinaction.chapter3;

import junit.framework.TestCase;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.dataformat.BindyType;
import org.junit.Test;

import java.math.BigDecimal;

public class PurchaseOrderBindyTest extends TestCase {

    public RouteBuilder createRoute() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:toCsv")
                        .marshal().bindy(BindyType.Csv, PurchaseOrderCsv.class)
                        .to("mock:result");
            }
        };
    }

    @Test
    public void testBindy() throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.addRoutes(createRoute());
        context.start();

        MockEndpoint mock = context.getEndpoint("mock:result", MockEndpoint.class);
        mock.expectedBodiesReceived("Camel in Action,49.95,1\n");

        PurchaseOrderCsv order = new PurchaseOrderCsv();
        order.setName("Camel in Action");
        order.setPrice(new BigDecimal("49.95"));
        order.setAmount(1);

        ProducerTemplate template = context.createProducerTemplate();
        template.sendBody("direct:toCsv", order);

        mock.assertIsSatisfied();
    }
}
