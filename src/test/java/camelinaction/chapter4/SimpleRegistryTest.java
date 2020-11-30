package camelinaction.chapter4;

import junit.framework.TestCase;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;

public class SimpleRegistryTest extends TestCase {
    private CamelContext context;
    private ProducerTemplate template;

    @Override
    protected void setUp() throws Exception {
        SimpleRegistry registry = new SimpleRegistry();
        registry.put("helloBean", new HelloBean());

        context = new DefaultCamelContext(registry);
        template = context.createProducerTemplate();

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:hello").beanRef("helloBean");
            }
        });

        context.start();
    }

    protected void tearDown() throws Exception {
        template.stop();
        context.stop();
    }

    public void testHello() throws Exception {
        Object reply = template.requestBody("direct:hello", "World");
        assertEquals("Hello World", reply);
    }
}
