package camelinaction.chapter14.middleware;

import camelinaction.chapter14.Inventory;
import camelinaction.chapter14.ShippingDetail;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

public class StarterKitTest extends CamelSpringTestSupport {
    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("camelinaction/chapter14/middleware/camel-context.xml");
    }

    @Test
    public void testStarterKitUpdateInventory() throws Exception {
        RiderService rider = context.getRegistry().lookupByNameAndType("rider", RiderService.class);
        updateInventory(rider);
    }

    private void updateInventory(RiderService rider) {
        Inventory inventory = new Inventory("1234", "4444");
        inventory.setName("Bumper");
        inventory.setAmount("57");

        log.info("Sending inventory");
        rider.updateInventory(inventory);
    }

    @Test
    public void testStarterShipping() throws Exception {
        RiderService rider = context.getRegistry().lookupByNameAndType("rider", RiderService.class);
        shipInventory(rider);
    }

    private void shipInventory(RiderService rider) {
        List<ShippingDetail> details = rider.shipInventory("1234", "4444");
        log.info("Received shipping details");

        assertEquals(2, details.size());
        assertEquals("Rider Road 66", details.get(0).getAddress());
        assertEquals("Ocean View 123", details.get(1).getAddress());
    }
}
