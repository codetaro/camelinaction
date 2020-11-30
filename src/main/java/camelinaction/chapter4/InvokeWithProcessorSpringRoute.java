package camelinaction.chapter4;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;

public class InvokeWithProcessorSpringRoute extends RouteBuilder {

    @Autowired
    private HelloBean hello;

    public void configure() throws Exception {
        from("direct:hello").bean(HelloBean.class);
/*
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        String name = exchange.getIn().getBody(String.class);
                        String answer = hello.hello(name);
                        exchange.getOut().setBody(answer);
                    }
                });
*/
    }
}
