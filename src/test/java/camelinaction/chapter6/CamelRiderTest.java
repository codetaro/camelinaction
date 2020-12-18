package camelinaction.chapter6;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;

public class CamelRiderTest extends CamelSpringTestSupport {

    private String inboxDir;
    private String outboxDir;

    @EndpointInject(uri = "file:{{file.inbox}}")
    private ProducerTemplate inbox;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        inboxDir = context.resolvePropertyPlaceholders("{{file.inbox}}");
        outboxDir = context.resolvePropertyPlaceholders("{{file.outbox}}");
    }

    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext(new String[] {
                "camelinaction/chapter6/rider-camel-prod.xml",
                "camelinaction/chapter6/rider-camel-test.xml"
        });
    }

    @Test
    public void testMoveFile() throws Exception {
        inbox.sendBodyAndHeader("Hello World",
                Exchange.FILE_NAME, "hello.txt");
        Thread.sleep(1000);

        File target = new File(outboxDir + "/hello.txt");
        assertTrue("File is moved", target.exists());

        String content = context.getTypeConverter().convertTo(String.class, target);
        assertEquals("Hello World", content);
    }
}
