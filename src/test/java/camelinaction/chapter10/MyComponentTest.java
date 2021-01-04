package camelinaction.chapter10;

import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import static org.junit.Assert.*;

public class MyComponentTest extends CamelTestSupport {

    @Test
    public void testMyComponent() throws Exception {
        context.addComponent("my", new MyComponent());

        context.getComponent("my", MyComponent.class).start();

        System.out.println("Waiting for 10 seconds before we shutdown");
        Thread.sleep(10 * 1000);
    }
}