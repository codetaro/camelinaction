package camelinaction.chapter8.aggregator;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.hawtdb.HawtDBAggregationRepository;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class AggregateABCRecoverTest extends CamelTestSupport {

    @Override
    public void setUp() throws Exception {
        deleteDirectory("data/myrepo.dat");
        super.setUp();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                HawtDBAggregationRepository hawtDB = new HawtDBAggregationRepository(
                        "myrepo", "data/myrepo.dat");
                hawtDB.setUseRecovery(true);
                hawtDB.setMaximumRedeliveries(4);
                hawtDB.setDeadLetterUri("mock:dead");
                hawtDB.setRecoveryInterval(3000);

                from("direct:start")
                        .log("Sending ${body} with correlation key ${header.myId}")
                        .aggregate(header("myId"), new MyAggregationStrategy())
                            .aggregationRepository(hawtDB)
                            .completionSize(3)
                            .log("Sending out ${body}")
                            // use a mock to check recovery
                            .to("mock:aggregate")
                            .throwException(new IllegalArgumentException("Damn does not work"))
                            .to("mock:result");
            }
        };
    }

    @Test
    public void testABCRecover() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(0);

        MockEndpoint mock = getMockEndpoint("mock:aggregate");
        mock.expectedMessageCount(5);

        MockEndpoint dead = getMockEndpoint("mock:dead");
        dead.expectedBodiesReceived("ABC");
        dead.message(0).header(Exchange.REDELIVERED).isEqualTo(true);
        dead.message(0).header(Exchange.REDELIVERY_COUNTER).isEqualTo(4);

        template.sendBodyAndHeader("direct:start", "A", "myId", 1);
        template.sendBodyAndHeader("direct:start", "B", "myId", 1);
        template.sendBodyAndHeader("direct:start", "C", "myId", 1);

        assertMockEndpointsSatisfied(20, TimeUnit.SECONDS);
    }
}
