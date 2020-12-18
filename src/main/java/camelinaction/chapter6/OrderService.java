package camelinaction.chapter6;

public class OrderService {

    public boolean validateOrder(String order) {
        return order.startsWith("123");
    }

    public String processOrder(String order) {
        if (order.endsWith("9999")) {
            throw new IllegalArgumentException("Forced error");
        }

        return "OK," + order;
    }
}
