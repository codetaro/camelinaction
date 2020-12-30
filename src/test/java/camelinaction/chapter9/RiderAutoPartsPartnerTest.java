package camelinaction.chapter9;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.net.ConnectException;

public class RiderAutoPartsPartnerTest extends CamelSpringTestSupport {

    private JdbcTemplate jdbc;

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("camelinaction/chapter9/RiderAutoPartsPartnerTest.xml");
    }

    @Before
    public void setupDatabase() {
        DataSource ds = context.getRegistry().lookupByNameAndType("myDataSource", DataSource.class);
        jdbc = new JdbcTemplate(ds);

        jdbc.execute("create table partner_metric "
                + "( partner_id varchar(10), time_occurred varchar(20), status_code varchar(3), perf_time varchar(10) )");
    }

    @After
    public void dropDatabase() {
        jdbc.execute("drop table partner_metric");
    }

    @Test
    public void testSendPartnerReportIntoDatabase() throws Exception {
        String sql = "select count(*) from partner_metric";
        assertEquals(0, jdbc.queryForInt(sql));

        String xml = "<?xml version='1.0'?><partner id='123'><date>200911150815</date><code>200</code><time>4387</time></partner>";
        template.sendBody("activemq:queue:partners", xml);

        Thread.sleep(5000);
        assertEquals(1, jdbc.queryForInt(sql));
    }

    @Test
    public void testNoConnectionToDatabase() throws Exception {
        // Simulates no connection to database
        RouteBuilder rb = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("jdbc:*")
                        .skipSendToOriginalEndpoint()
                        .throwException(new ConnectException("Cannot connect to the database"));
            }
        };

        // Advises simulation into existing route
        RouteDefinition route = context.getRouteDefinition("partnerToDB");
        route.adviceWith(context, rb);

        String sql = "select count(*) from partner_metric";
        assertEquals(0, jdbc.queryForInt(sql));

        String xml = "<?xml version='1.0'?>"
                + "<partner id='123'><date>200911150815</date>"
                + "<code>200</code><time>4837</time></partner>";
        template.sendBody("activemq:queue:partners", xml);

        Thread.sleep(15000);
        // Asserts no rows inserted into database
        assertEquals(0, jdbc.queryForInt(sql));

        // check that the message was moved to the DLQ
        String dlq = consumer.receiveBody("activemq:queue:ActiveMQ.DLQ", 1000L, String.class);
        assertNotNull("Message should have been moved to the ActiveMQ DLQ", dlq);
    }

    @Test
    public void testFailFirstTime() throws Exception {
        RouteBuilder rb = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("jdbc:*")
                        // Content-Based Router EIP
                        .choice()
                            .when(header("JMSRedelivered").isEqualTo("false"))
                                .throwException(new ConnectException("Cannot connect to the database"))
                        .end();
            }
        };
        context.getRouteDefinition("partnerToDB").adviceWith(context, rb);

        String sql = "select count(*) from partner_metric";
        assertEquals(0, jdbc.queryForInt(sql));

        String xml = "<?xml version='1.0'?>"
                + "<partner id='123'><date>202012301205</date>"
                + "<code>200</code><time>4387</time></partner>";
        template.sendBody("activemq:queue:partners", xml);

        Thread.sleep(5000);
        assertEquals(1, jdbc.queryForInt(sql));

        Object dlq = consumer.receiveBodyNoWait("activemq:queue:ActiveMQ.DLQ");
        assertNull("Should not be in the DLQ", dlq);
    }
}
