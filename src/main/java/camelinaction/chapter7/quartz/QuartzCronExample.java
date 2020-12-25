package camelinaction.chapter7.quartz;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class QuartzCronExample {

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("quartz://report?cron=0/2+*+*+*+*+?")
                        .setBody().simple("I was fired at ${header.fireTime}")
                        .to("stream:out");
            }
        });

        context.start();
        Thread.sleep(10000);

        context.stop();
    }
}
