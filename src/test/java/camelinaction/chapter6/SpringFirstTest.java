package camelinaction.chapter6;

import org.apache.camel.Exchange;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;

public class SpringFirstTest extends CamelSpringTestSupport {

    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext(
                "camelinaction/chapter6/firststep.xml");
    }

    @Test
    public void testMoveFile() throws Exception {
        template.sendBodyAndHeader("file://data/inbox", "Hello World",
                Exchange.FILE_NAME, "hello.txt");
        Thread.sleep(1000);

        File target = new File("data/outbox/hello.txt");
        assertTrue("File is moved", target.exists());

        String content = context.getTypeConverter().convertTo(String.class, target);
        assertEquals("Hello World", content);
    }
}
