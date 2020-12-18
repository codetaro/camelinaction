package camelinaction.chapter6;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.util.Properties;

public class SpringRiderTest extends CamelSpringTestSupport {

    private String inboxDir;
    private String outboxDir;

    @EndpointInject(ref = "inbox")
    private ProducerTemplate inbox;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        Properties prop = context.getRegistry().lookup("properties", Properties.class);
        inboxDir = prop.getProperty("file.inbox");
        outboxDir = prop.getProperty("file.outbox");

//        deleteDirectory(inboxDir);
//        deleteDirectory(outboxDir);
    }

    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext(new String[]{
                "camelinaction/chapter6/rider-spring-prod.xml",
                "camelinaction/chapter6/rider-spring-test.xml"
        });
    }

    @Test
    public void testMoveFile() throws Exception {
        context.setTracing(true);

        inbox.sendBodyAndHeader("Hello World", Exchange.FILE_NAME, "hello.txt");

        Thread.sleep(2000);

        File target = new File(outboxDir + "/hello.txt");
        assertTrue("File should have been moved", target.exists());

        String content = context.getTypeConverter().convertTo(String.class, target);
        assertEquals("Hello World", content);
    }
}
