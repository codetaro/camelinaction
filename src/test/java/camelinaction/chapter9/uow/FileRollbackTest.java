package camelinaction.chapter9.uow;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.io.File;

public class FileRollbackTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:confirm")
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                // register the FileRollback as Synchronization
                                exchange.getUnitOfWork().addSynchronization(new FileRollback());

                                // this can be done a bit easier by using:
//                                exchange.addOnCompletion(new FileRollback());
                            }
                        })
                        .bean(OrderService.class, "createMail")
                        .log("Saving mail backup file")
                        .to("file:data/mail/backup")
                        .log("Trying to send mail to ${header.to}")
                        .bean(OrderService.class, "sendMail")
                        .log("Mail send to ${header.to}");
            }
        };
    }

    @Override
    public void setUp() throws Exception {
        deleteDirectory("data/mail/backup");
        super.setUp();
    }

    @Test
    public void testOk() throws Exception {
        template.sendBodyAndHeader("direct:confirm", "bumper", "to", "someone@somewhere.org");

        File file = new File("data/mail/backup/");
        String[] files = file.list();
        assertEquals("There should be one file", 1, files.length);
    }

    @Test
    public void testRollback() throws Exception {
        try {
            template.sendBodyAndHeader("direct:confirm", "bumper", "to", "FATAL");
            fail("Should have thrown an exception");
        } catch (CamelExecutionException e) {
            assertIsInstanceOf(IllegalArgumentException.class, e.getCause());
            assertEquals("Simulated fatal error", e.getCause().getMessage());
        }

        File file = new File("data/mail/backup/");
        String[] files = file.list();
        assertEquals("There should be no files", 0, files.length);
    }
}
