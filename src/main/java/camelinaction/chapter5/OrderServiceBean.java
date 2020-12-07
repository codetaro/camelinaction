package camelinaction.chapter5;

import java.io.FileNotFoundException;
import java.net.ConnectException;

public class OrderServiceBean {

    public String handleOrder(String body) throws OrderFailedException {
        if (body.contains("ActiveMQ")) {
            throw new OrderFailedException("Cannot order ActiveMQ");
        }

        return body + ",id=123";
    }

    public String saveToDB(String order) throws OrderFailedException {
        throw new OrderFailedException("Cannot store in DB", new ConnectException("Cannot connect to DB"));
    }

    public String enrichFromFile(String order) throws OrderFailedException {
        throw new OrderFailedException("Cannot load file", new FileNotFoundException("Cannot find file"));
    }
}
