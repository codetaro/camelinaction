package camelinaction.chapter12.health;

import org.apache.camel.builder.RouteBuilder;

public class PingService extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("jetty:http://0.0.0.0:8080/ping").transform(constant("PONG\n"));
    }
}
