package chapter2;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

import javax.jms.ConnectionFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FtpToJMSExample {

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();

        ConnectionFactory connectionFactory =
                new ActiveMQConnectionFactory("vm://localhost");
        context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file:data/inbox?noop=true").to("jms:queue:incomingOrders");

                from("jms:queue:incomingOrders")
                    .wireTap("jms:orderAudit")
                    .choice()
                        .when(header("CamelFileName").endsWith(".xml"))
                            .to("jms:xmlOrders")
                        .when(header("CamelFileName").regex("^.*(csv|csl)$"))
                            .to("jms:csvOrders")
                        .otherwise()
                            .to("jms:badOrders").stop()
                    .end()
                    .to("jms:continuedProcessing");

                ExecutorService executor = Executors.newFixedThreadPool(16);

/*
                // Using multicasting
                from("jms:xmlOrders").filter(xpath("/order[not(@test)]"))
                        .multicast().stopOnException()
                        .parallelProcessing().executorService(executor)
                        .to("jms:accounting" ,"jms:production");
*/
                // Using recipient lists
                from("jms:xmlOrders").bean(RecipientListBean.class);

                from("jms:accounting").process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        System.out.println("Accounting received XML order: "
                                + exchange.getIn().getHeader("CamelFileName"));
                    }
                });
                from("jms:production").process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        System.out.println("Production received XML order: "
                                + exchange.getIn().getHeader("CamelFileName"));
                    }
                });

                from("jms:csvOrders").process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        System.out.println("Received CSV order: "
                                + exchange.getIn().getHeader("CamelFileName"));
                    }
                });
                from("jms:badOrders").process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        System.out.println("Received bad order: "
                                + exchange.getIn().getHeader("CamelFileName"));
                    }
                });
            }
        });
        context.start();
        Thread.sleep(10000);

        context.stop();
    }
}
