package camelinaction.chapter10.scalability;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.event.EventComponent;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import static org.junit.Assert.*;

public class ScalabilityTest extends CamelTestSupport {

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext context = super.createCamelContext();

        // add the ERP component
        context.addComponent("erp", new ErpComponent());

        return context;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("jetty:http://localhost:8090/webshop?matchOnUriPrefix=true")
                        .convertBodyTo(String.class)
                        .log("log:input")
                        .choice()
                            .when(header(Exchange.HTTP_PATH).isEqualTo("/action/pricing"))
                                .to("erp:pricing")
                            .otherwise()
                                .to("direct:other")
                        .end()
                        .log("log:output");

                from("direct:other")
                        .transform(constant("Some other action here"));
            }
        };
    }

    @Test
    public void testSync() throws Exception {
        String reply = template.requestBody("http://localhost:8090/webshop/action/search", "bumper", String.class);
        assertEquals("Some other action here", reply);
        // the threads being used to log the input/output is the same thread
    }

    @Test
    public void testAsync() throws Exception {
        String reply = template.requestBody("http://localhost:8090/webshop/action/pricing", "1234;4;1719;bumper", String.class);
        assertEquals("1234;4;1719;bumper;516", reply);
        // the threads being used to log the input/output are different threads
    }
}