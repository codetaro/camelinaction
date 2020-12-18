package camelinaction.chapter6;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.io.File;

public class FirstTest extends CamelTestSupport {

    @Override
    public void setUp() throws Exception {
//        deleteDirectory("data/inbox");
//        deleteDirectory("data/outbox");
        super.setUp();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file://data/inbox").to("file://data/outbox");
            }
        };
//        return new FileMoveRoute();
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
