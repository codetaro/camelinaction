package camelinaction.chapter12.custom;

import org.apache.camel.builder.RouteBuilder;

public class ERPRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("timer:foo?period=5000")
                .setBody().simple("Hello ERP calling at ${date:now:HH:mm:ss}")
                .to("erp:foo")
                .to("log:reply");
    }
}
