package camelinaction.chapter6;

import org.apache.camel.builder.RouteBuilder;

public class FileMoveRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("file://data/inbox").to("file://data/outbox");
    }
}
