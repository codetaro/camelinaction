package camelinaction.chapter6;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.net.ConnectException;

public class SimulateErrorUsingInterceptorTest extends CamelSpringTestSupport {

    @EndpointInject(ref = "fileEndpoint")
    private Endpoint file;

    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("camelinaction/usecase.xml");
    }

    @Test
    public void testSimulateErrorUsingInterceptors() throws Exception {
        // find the route we need to advice
        RouteDefinition route = context.getRouteDefinitions().get(0);

        route.adviceWith(context, new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // intercept sending to http and detour to our processor instead
                interceptSendToEndpoint("http://*")
                        .skipSendToOriginalEndpoint()
                        .process(new SimulateHttpErrorProcessor());

                // intercept sending to ftp and detour to the mock instead
                interceptSendToEndpoint("ftp://*")
                        .skipSendToOriginalEndpoint()
                        .to("mock:ftp");
            }
        });

        MockEndpoint mock = getMockEndpoint("mock:ftp");
        mock.expectedBodiesReceived("Camel rocks");

        template.sendBodyAndHeader(file, "Camel rocks", Exchange.FILE_NAME, "hello.txt");

        assertMockEndpointsSatisfied();
    }

    private class SimulateHttpErrorProcessor implements Processor {
        public void process(Exchange exchange) throws Exception {
            throw new ConnectException("Simulated connection error");
        }
    }
}
