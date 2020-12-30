package camelinaction.chapter9;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.SpringRouteBuilder;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TXToNonTXTest extends CamelSpringTestSupport {
    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("camelinaction/chapter9/TXToNonTXTest.xml");
    }

    // indicate to CamelSpringTestSupport that the Spring XML file doesn't contain any routes
    protected int getExpectedRouteCount() {
        return 0;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new SpringRouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("activemq:queue:a")
                        .transacted()
                        .to("direct:quote")
                        .to("activemq:queue:b");

                from("direct:quote")
                        .choice()
                            .when(body().contains("Camel"))
                                .transform(constant("Camel rocks"))
                            .when(body().contains("Donkey"))
                                .throwException(new IllegalArgumentException("Donkeys not allowed"))
                        .otherwise()
                            .transform(body().prepend("Hello "));
            }
        };
    }

    @Test
    public void testWithCamel() throws Exception {
        template.sendBody("activemq:queue:a", "Hi Camel");
        Object reply = consumer.receiveBody("activemq:queue:b", 10000);
        assertEquals("Camel rocks", reply);
    }

    @Test
    public void testWithDonkey() throws Exception {
        template.sendBody("activemq:queue:a", "Donkey");
        Object reply = consumer.receiveBody("activemq:queue:b", 10000);
        assertNull("There should be no reply", reply);

        reply = consumer.receiveBody("activemq:queue:ActiveMQ.DLQ", 10000);
        assertNotNull("It should have been moved to DLQ", reply);
    }
}
