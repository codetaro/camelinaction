package camelinaction.chapter12.tracer;

import camelinaction.chapter12.logging.AuditService;
import camelinaction.chapter12.logging.OrderCsvToXmlBean;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;

import java.util.concurrent.TimeUnit;

public class ManageTracerMain {

    private CamelContext context;
    private ProducerTemplate template;

    public static void main(String[] args) throws Exception {
        ManageTracerMain main = new ManageTracerMain();
        main.run();
    }

    public void run() throws Exception {
        context = createCamelContext();
        context.addRoutes(createRouteBuilder());
        template = context.createProducerTemplate();
        context.start();
        testManageTracer();
        template.stop();
        context.stop();
    }

    public void testManageTracer() throws Exception {
        System.out.println("Connect to JConsole and try managing Tracer by enabling and disabling it on individual routes");

        MockEndpoint mock = context.getEndpoint("jms:queue:orders", MockEndpoint.class);
        mock.expectedMessageCount(100);

        for (int i = 0; i < 100; i++) {
            template.sendBody("file://data/orders", "" + i + ",4444,20100110,222,1");
        }

        mock.await(100 * 10, TimeUnit.SECONDS);

        System.out.println("Complete sending 100 files will stop now");
    }

    protected CamelContext createCamelContext() throws Exception {
        CamelContext answer = new DefaultCamelContext();
        // simulate JMS with the Mock component
        answer.addComponent("jms", answer.getComponent("mock"));

        // enable connector for remote management
        answer.getManagementStrategy().getManagementAgent().setCreateConnector(true);

        return answer;
    }

    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // enable tracing
                context.setTracing(true);
                context.setDelayer(2000L);

                from("file://data/orders")
                        .wireTap("seda:audit")
                        .bean(OrderCsvToXmlBean.class)
                        .to("jms:queue:orders");

                from("seda:audit")
                        .bean(AuditService.class, "auditFile");
            }
        };
    }
}
