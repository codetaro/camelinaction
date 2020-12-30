package camelinaction.chapter9;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class AtomikosXACommitTest extends CamelSpringTestSupport {

    private JdbcTemplate jdbc;

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("camelinaction/chapter9/spring-context-atomikos.xml");
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("activemq:queue:partners")
                        .routeId("partnerToDB")
                        .transacted()
                        .bean(PartnerServiceBean.class, "toSql")
                        .to("jdbc:myDataSource?resetAutoCommit=false")
                        .to("mock:result");
            }
        };
    }

    @Before
    public void setupDatabase() throws Exception {
        DataSource ds = context.getRegistry().lookupByNameAndType("myDataSource", DataSource.class);
        jdbc = new JdbcTemplate(ds);

        jdbc.execute("create table partner_metric "
                + "( partner_id varchar(10), time_occurred varchar(20), status_code varchar(3), perf_time varchar(10) )");
    }

    @After
    public void dropDatabase() throws Exception {
        jdbc.execute("drop table partner_metric");
    }

    @Test
    public void testXaCommit() throws Exception {
        assertEquals(0, jdbc.queryForInt("select count(*) from partner_metric"));

        String xml = "<?xml version='1.0'?><partner id='123'><date>202012310116</date><code>200</code><time>4387</time></partner>";
        template.sendBody("activemq:queue:partners", xml);

        Thread.sleep(5000);

        assertEquals(1, jdbc.queryForInt("select count(*) from partner_metric"));
    }

    @Test
    public void testXaRollbackAfterDb() throws Exception {
        RouteBuilder rb = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("mock:*")
                        .skipSendToOriginalEndpoint()
                        .throwException(new IllegalArgumentException("Forced failure after DB"));
            }
        };
        context.getRouteDefinition("partnerToDB").adviceWith(context, rb);

        assertEquals(0, jdbc.queryForInt("select count(*) from partner_metric"));

        String xml = "<?xml version='1.0'?><partner id='123'><date>202012310116</date><code>200</code><time>4387</time></partner>";
        template.sendBody("activemq:queue:partners", xml);

        Thread.sleep(5000);

        assertEquals(0, jdbc.queryForInt("select count(*) from partner_metric"));

        // should be in DLQ
        String dlq = consumer.receiveBodyNoWait("activemq:queue:ActiveMQ.DLQ", String.class);
        assertNotNull("Should not loose message", dlq);
    }

    @Test
    public void testXaRollbackBeforeDb() throws Exception {
        assertEquals(0, jdbc.queryForInt("select count(*) from partner_metric"));

        // partner id as 0 will cause rollback
        String xml = "<?xml version='1.0'?><partner id='0'><date>200911150927</date><code>500</code><time>8732</time></partner>";
        template.sendBody("activemq:queue:partners", xml);

        // wait for the route to complete with failure
        Thread.sleep(15000);

        assertEquals(0, jdbc.queryForInt("select count(*) from partner_metric"));

        // should be in DLQ
        String dlq = consumer.receiveBodyNoWait("activemq:queue:ActiveMQ.DLQ", String.class);
        assertNotNull("Should not loose message", dlq);
    }
}
