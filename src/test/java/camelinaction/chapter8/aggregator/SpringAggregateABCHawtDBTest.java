package camelinaction.chapter8.aggregator;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringAggregateABCHawtDBTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("camelinaction/chapter8/aggregate-abc-hawtdb.xml");
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
