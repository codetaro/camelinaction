package camelinaction.chapter6;

import org.apache.activemq.ActiveMQConnectionFactory;


import javax.jms.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderClient {

    private final ActiveMQConnectionFactory fac;

    public OrderClient(String url) {
        this.fac = new ActiveMQConnectionFactory(url);
    }

    public void sendOrder(int customerId, Date date, String... itemIds) throws Exception {
        String d = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(date);
        String body = customerId + "," + d;
        for (String id : itemIds) {
            body += "," + id;
        }

        Connection con = fac.createConnection();
        con.start();
        Session ses = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination dest = ses.createQueue("order");
        MessageProducer prod = ses.createProducer(dest);
        prod.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        Message msg = ses.createTextMessage(body);
        prod.send(msg);
        prod.close();
        ses.close();
        con.close();
    }
}
