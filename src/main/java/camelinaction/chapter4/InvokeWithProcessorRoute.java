package camelinaction.chapter4;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class InvokeWithProcessorRoute extends RouteBuilder {

    public void configure() throws Exception {

        from("direct:hello")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        String name = exchange.getIn().getBody(String.class);

                        HelloBean hello = new HelloBean();
                        String answer = hello.hello(name);

                        exchange.getOut().setBody(answer);
                    }
                });
    }
}
