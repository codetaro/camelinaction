package camelinaction.chapter7.seda;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

import javax.jms.ConnectionFactory;

public class OrderRouterWithSeda {

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
        context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file:data?noop=true").to("seda:incomingOrders");

                // content-based router
                from("seda:incomingOrders")
                        .choice()
                        .when(header("CamelFileName").endsWith(".xml"))
                            .to("seda:xmlOrders")
                        .when(header("CamelFileName").endsWith(".csv"))
                            .to("seda:csvOrders");

                from("seda:xmlOrders?multipleConsumers=true").to("jms:accounting");
                from("seda:xmlOrders?multipleConsumers=true").to("jms:production");

                // test that the route is working
                from("jms:accounting").process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        System.out.println("Accounting received order: "
                                + exchange.getIn().getHeader("CamelFileName"));
                    }
                });
                from("jms:production").process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        System.out.println("Production received order: "
                                + exchange.getIn().getHeader("CamelFileName"));
                    }
                });
            }
        });

        // start the route and let it do its work
        context.start();
        Thread.sleep(10000);

        //
        context.stop();
    }
}
