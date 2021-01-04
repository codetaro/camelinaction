package camelinaction.chapter10;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MyComponent extends DefaultComponent implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(MyComponent.class);
    private ScheduledExecutorService executor;

    @Override
    protected Endpoint createEndpoint(String s, String s1, Map<String, Object> map) throws Exception {
        return null;
    }

    @Override
    public void run() {
        // this is the task being executed every second
        LOG.info("I run now");
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        // create a scheduled thread pool with 1 thread as we only need one task as background task
        executor = getCamelContext().getExecutorServiceManager().newScheduledThreadPool(this, "MyBackgroundTask", 1);
        // schedule the task to run once every second
        executor.scheduleWithFixedDelay(this, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    protected void doStop() throws Exception {
        // shutdown the thread pool
        getCamelContext().getExecutorServiceManager().shutdown(executor);
        super.doStop();
    }
}
