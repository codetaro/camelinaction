package camelinaction.chapter8.aggregator;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.hawtdb.HawtDBAggregationRepository;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class AggregateABCHawtDBTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                HawtDBAggregationRepository hawtDB = new HawtDBAggregationRepository("myrepo", "data/myrepo.dat");

                from("file:data/inbox")
                        .log("Consuming file ${file:name}")
                        .convertBodyTo(String.class)
                        .aggregate(constant(true), new MyAggregationStrategy())
                        .aggregationRepository(hawtDB)
                        .completionSize(3)
                        .log("Sending out ${body}")
                        .to("mock:result");
            }
        };
    }


    @Test
    public void testABCHawtDB() throws Exception {
        System.out.println("Copy 3 files to data/inbox to trigger the completion");
        System.out.println("Files to copy:");
        System.out.println("  copy src/test/resources/a.txt data/inbox");
        System.out.println("  copy src/test/resources/b.txt data/inbox");
        System.out.println("  copy src/test/resources/c.txt data/inbox");
        System.out.println("\nSleeping for 20 seconds");
        System.out.println("You can let the test terminate (or press ctrl+c) and then start it again");
        System.out.println("Which should let you be able to resume.");

        Thread.sleep(20 * 1000);
    }
}
