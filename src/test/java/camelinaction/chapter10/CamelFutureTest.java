package camelinaction.chapter10;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CamelFutureTest extends CamelTestSupport {

    private static Logger LOG = LoggerFactory.getLogger(CamelFutureTest.class);

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("seda:quote")
                        .log("Starting to route ${body}")
                        .delay(5000)
                        .transform().constant("Camel rocks")
                        .log("Route is now done");
            }
        };
    }

    @Test
    public void testFutureWithoutDone() throws Exception {
        LOG.info("Submitting task to Camel");
        Future<String> future = template.asyncRequestBody("seda:quote", "Hello Camel", String.class);

        LOG.info("Task submitted and we got a Future handle");

        String answer = future.get();
        LOG.info("The answer is: " + answer);
    }

    @Test
    public void testFutureWithDone() throws Exception {
        // Creates task
        Callable<String> task = new Callable<String>() {
            @Override
            public String call() throws Exception {
                LOG.info("Starting to process task");
                Thread.sleep(5000);
                LOG.info("Task is now done");
                return "Camel rocks";
            }
        };

        // Submits task
        LOG.info("Submitting task to ExecutorService");
        ExecutorService executor = Executors.newCachedThreadPool();
        Future<String> future = executor.submit(task);
        LOG.info("Task submitted and we got a Future handle");

        // Waits until task is done
/*
        boolean done = false;
        while (!done) {
            done = future.isDone();
            LOG.info("Is the task done? " + done);
            if (!done) {
                Thread.sleep(2000);
            }
        }
*/

        // Gets task result
        String answer = future.get();
        LOG.info("The answer is: " + answer);
    }
}
