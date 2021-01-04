package camelinaction.chapter10;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MulticastParallelTest extends CamelSpringTestSupport {
    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("camelinaction/chapter10/route-parallel.xml");
    }

    @Test
    public void testMulticastParallel() {
        String out = template.requestBody("direct:portal", "123", String.class);
        System.out.println(out);
    }
}
