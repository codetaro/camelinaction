package camelinaction.chapter10.synchronicity;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class SyncMultipleThreadsTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("seda:start")
                        .to("log:A")
                        .threads(5, 10)
                        .transform(constant("Bye Camel"))
                        .to("log:B");
            }
        };
    }

    @Test
    public void testSyncInOut() throws Exception {
        String body = "Hello Camel";

        log.info("Caller calling Camel with message: " + body);
        String reply = template.requestBody("seda:start", body, String.class);

        assertEquals("Bye Camel", reply);
        log.info("Caller finished calling Camel and received reply: " + reply);

        Thread.sleep(1000);
    }
}
