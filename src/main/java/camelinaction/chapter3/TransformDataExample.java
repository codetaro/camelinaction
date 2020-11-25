package camelinaction.chapter3;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import javax.jms.ConnectionFactory;

public class TransformDataExample {

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();

        ConnectionFactory connectionFactory =
                new ActiveMQConnectionFactory("vm://localhost");
        context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("quartz://report?cron=0+0+6+*+*+?")
                        .to("http://riders.com/orders/cmd=received")
                        .process(new OrderToCsvProcessor())
                        .pollEnrich("ftp://riders.com/orders/?username=rider&password=secret",
                                new AggregationStrategy() {
                                    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
                                        // return the existing data, if no remote file to consume
                                        if (newExchange == null) {
                                            return oldExchange;
                                        }

                                        String http = oldExchange.getIn().getBody(String.class);
                                        String ftp = newExchange.getIn().getBody(String.class);
                                        String body = http + "\n" + ftp;
                                        oldExchange.getIn().setBody(body);

                                        return oldExchange;
                                    }
                                })
                        .to("file://riders/orders");

                from("file://rider/inbox")
                        .to("xslt://camelinaction.chapter3/transform.xsl")
                        .to("activemq:queue:transformed");
            }
        });
        context.start();
        Thread.sleep(10000);

        context.stop();
    }
}
