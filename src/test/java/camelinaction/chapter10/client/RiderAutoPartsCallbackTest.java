package camelinaction.chapter10.client;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.Synchronization;
import org.apache.camel.support.SynchronizationAdapter;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RiderAutoPartsCallbackTest extends CamelTestSupport {

    private int numPartners = 5;

    @Test
    public void testCallback() throws Exception {
        final List<String> relates = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(numPartners);

        Synchronization callback = new SynchronizationAdapter() {
            @Override
            public void onComplete(Exchange exchange) {
                relates.add(exchange.getOut().getBody(String.class));
                latch.countDown();
            }

            @Override
            public void onFailure(Exchange exchange) {
                latch.countDown();
            }
        };

        String body = "bumper";
        for (int i = 0; i < numPartners; i++) {
            template.asyncCallbackRequestBody("seda:partner:" + i, body, callback);
        }
        log.info("Send " + numPartners + " messages to partners.");

        boolean all = latch.await(1500, TimeUnit.MILLISECONDS);

        log.info("Got " + relates.size() + " replies, is all? " + all);
        for (String related : relates) {
            log.info("Related item category is: " + related);
        }
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // business partner routes with different delays
                from("seda:partner:0").delay(500).transform().simple("bumper filter");

                from("seda:partner:1").delay(3000).transform().simple("nose panel");

                from("seda:partner:2").delay(1000).transform().simple("bumper cover");

                from("seda:partner:3").delay(250).transform().simple("bumper extension");

                from("seda:partner:4").delay(4000).transform().simple("tow hooks");
            }
        };
    }
}
