package camelinaction.chapter4;

import org.apache.camel.test.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class InvokeWithProcessorSpringTest extends CamelSpringTestSupport {

    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("camelinaction/chapter4/InvokeWithProcessor.xml");
    }

    @Test
    public void testHelloBean() throws Exception {
        String reply = template.requestBody("direct:hello", "Camel in action", String.class);
        assertEquals("Hello Camel in action", reply);
    }
}