package camelinaction.chapter10.synchronicity;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class AsyncMultipleThreadsTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("seda:start")
                        .to("log:A")
                        .threads(5, 10)
                        .to("log:B");
            }
        };
    }

    @Test
    public void testAsyncInOnly() throws Exception {
        String body = "Hello Camel";

        log.info("Caller calling Camel with message: " + body);
        template.sendBody("seda:start", body);
        log.info("Caller finished calling Camel");

        Thread.sleep(1000);
    }
}
