package camelinaction.chapter6;

import org.apache.camel.Exchange;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.spi.BrowsableEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class OrderTest extends CamelSpringTestSupport {

    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/camel-context.xml");
    }

    @Test
    public void testOrderClientValid() throws Exception {

        NotifyBuilder notify = new NotifyBuilder(context)
                .from("activemq:queue:order")
                .whenAnyDoneMatches(
                        body().isEqualTo("OK,123,2020-12-17T15:02:58,4444,5555"))
//                .whenDone(1)
                .create();

        Calendar cal = Calendar.getInstance(Locale.US);
        cal.set(Calendar.YEAR, 2020);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 17);
        cal.set(Calendar.HOUR_OF_DAY, 15);
        cal.set(Calendar.MINUTE, 2);
        cal.set(Calendar.SECOND, 58);
        Date date = cal.getTime();

        OrderClient client = new OrderClient("tcp://localhost:61616");
        client.sendOrder(123, date, "4444", "5555");

        // wait at most 5 seconds for Camel to process the message
        boolean matches = notify.matches(5, TimeUnit.SECONDS);
        // true means the notifier condition matched (= 1 message is done)
        assertTrue(matches);
        ;

        BrowsableEndpoint be = context.getEndpoint("activemq:queue:confirm", BrowsableEndpoint.class);
        List<Exchange> list = be.getExchanges();
        assertEquals(1, list.size());
        assertEquals("OK,123,2020-12-17T15:02:58,4444,5555", list.get(0).getIn().getBody(String.class));
    }

    @Test
    public void testOrderClientInvalid() throws Exception {
        NotifyBuilder notify = new NotifyBuilder(context).whenDone(1).create();

        Calendar cal = Calendar.getInstance(Locale.US);
        cal.set(Calendar.YEAR, 2020);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 17);
        cal.set(Calendar.HOUR_OF_DAY, 15);
        cal.set(Calendar.MINUTE, 55);
        cal.set(Calendar.SECOND, 00);
        Date date = cal.getTime();

        OrderClient client = new OrderClient("tcp://localhost:61616");
        client.sendOrder(999, date, "5555", "2222");

        boolean matches = notify.matches(5, TimeUnit.SECONDS);
        assertTrue(matches);

        BrowsableEndpoint be = context.getEndpoint("activemq:queue:invalid", BrowsableEndpoint.class);
        List<Exchange> list = be.getExchanges();
        assertEquals(1, list.size());
        assertEquals("999,2020-12-17T15:55:00,5555,2222", list.get(0).getIn().getBody(String.class));
    }

    @Test
    public void testOrderClientFailure() throws Exception {
        NotifyBuilder notify = new NotifyBuilder(context).whenFailed(1).create();

        Calendar cal = Calendar.getInstance(Locale.US);
        cal.set(Calendar.YEAR, 2020);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 17);
        cal.set(Calendar.HOUR_OF_DAY, 16);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        Date date = cal.getTime();

        OrderClient client = new OrderClient("tcp://localhost:61616");
        // by using 9999 as the last item id we force an exception to occur
        client.sendOrder(123, date, "4444", "9999");

        boolean matches = notify.matches(5, TimeUnit.SECONDS);
        assertTrue(matches);
    }
}
